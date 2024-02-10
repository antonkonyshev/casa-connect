package name.antonkonyshev.home.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.data.database.DeviceRepositoryImpl
import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DiscoveryService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address

object DiscoveryServiceImpl : DiscoveryService {
    private var scanning = false
    private val scope = CoroutineScope(Dispatchers.IO)
    private val adapter by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(DeviceModel::class.java)
    }

    override fun discoverDevices() {
        if (scanning) {
            return
        }
        scanning = true
        scope.async {
            val connectivityManager = HomeApplication.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find { la -> la.address is Inet4Address }
            DeviceRepositoryImpl.updateAllDevicesAvailability(false)
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
                                            DeviceRepositoryImpl.updateStateOrCreate(device)
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