package name.antonkonyshev.home.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DeviceRepository
import name.antonkonyshev.home.domain.repository.DiscoveryService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoveryServiceImpl @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val moshi: Moshi
) : DiscoveryService {
    private var scanning = false
    private val adapter by lazy { moshi.adapter(DeviceModel::class.java) }

    override fun discoverDevices() {
        if (scanning) {
            return
        }
        scanning = true
        CoroutineScope(Dispatchers.IO).async {
            val connectivityManager = HomeApplication.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find { la -> la.address is Inet4Address }
            deviceRepository.updateAllDevicesAvailability(false)
            if (linkAddress is LinkAddress) {
                val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
                    it != linkAddress.address.hostAddress
                }
                ipRange.forEach { ipAddress ->
                    val ip = Inet4Address.getByName(ipAddress)
                    if (ip.isMulticastAddress) {
                        return@forEach
                    }
                    CoroutineScope(Dispatchers.IO).async {
                        if (ip.isReachable(1000)) {
                            OkHttpClient().newCall(
                                Request.Builder().url("http://" + ip.hostAddress + "/service")
                                    .build()
                            ).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {}
                                override fun onResponse(call: Call, response: Response) {
                                    if (response.code == 200) {
                                        val device = adapter.fromJson(response.body!!.source())
                                        response.body!!.close()
                                        if (device is DeviceModel) {
                                            device.ip = ip
                                            deviceRepository.updateStateOrCreate(device)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            }

            scanning = false
        }

    }
}