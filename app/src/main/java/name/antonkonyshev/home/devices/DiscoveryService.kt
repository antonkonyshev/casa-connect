package name.antonkonyshev.home.devices

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import name.antonkonyshev.home.HomeApplication
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address

class DiscoveryService(val application: HomeApplication) {
    private var scanning = false
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        @Volatile
        private var _instance: DiscoveryService? = null

        fun instance(application: HomeApplication): DiscoveryService {
            return (_instance ?: synchronized(this) {
                _instance = DiscoveryService(application)
                return@synchronized _instance
            })!!
        }
    }

    fun discoverDevices() {
        if (scanning) { return }
        scanning = true
        scope.async {
            val connectivityManager = application.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find { la -> la.address is Inet4Address }
            val deviceRepository = application.deviceRepository
            if (linkAddress is LinkAddress) {
                val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
                    it != linkAddress.address.hostAddress
                }
                // devices.value.forEach { it.available = false }
                ipRange.forEach { ipAddress ->
                    val ip = Inet4Address.getByName(ipAddress)
                    if (ip.isMulticastAddress()) {
                        return@forEach
                    }
                    CoroutineScope(Dispatchers.IO).async {
                        if (ip.isReachable(1000)) {
                            val response = OkHttpClient().newCall(
                                Request.Builder().url("http://" + ip.hostAddress + "/service")
                                    .build()
                            ).enqueue(object: Callback {
                                override fun onFailure(call: Call, e: IOException) {}
                                override fun onResponse(call: Call, response: Response) {
                                    if (response.code == 200) {
                                        val moshi = Moshi.Builder()
                                            .add(KotlinJsonAdapterFactory()).build()
                                        val device = moshi.adapter(Device::class.java)
                                            .fromJson(response.body!!.source())
                                        response.body!!.close()
                                        if (device is Device) {
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

    fun discoverLocalDevices(linkAddress: LinkAddress, devices: MutableStateFlow<List<Device>>) {
    }
}