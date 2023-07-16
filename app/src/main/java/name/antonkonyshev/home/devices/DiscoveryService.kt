package name.antonkonyshev.home.devices

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

object DiscoveryService {
    fun discoverLocalDevices(linkAddress: LinkAddress, devices: MutableStateFlow<List<Device>>) {
        val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
            it != linkAddress.address.hostAddress
        }
        devices.value.forEach { it.available = false }

        // TODO: remove testing device
        devices.update {
            val updatedDevices = it.toMutableList()
            updatedDevices.add(
                Device(
                    "testing-device-1", "meteo", "Outdoors",
                    listOf("temperature", "altitude", "pressure"),
                    Inet4Address.getByName("192.168.0.100"), false
                )
            )
            return@update updatedDevices.toList()
        }
        // TODO: remove testing device
        devices.update {
            val updatedDevices = it.toMutableList()
            updatedDevices.add(
                Device(
                    "testing-device-2", "meteo", "Kitchen",
                    listOf("temperature", "humidity", "pressure"),
                    Inet4Address.getByName("192.168.0.140"), true
                )
            )
            return@update updatedDevices.toList()
        }

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
                                if (device is Device) {
                                    val existingDevice =
                                        devices.value.find { it.id == device.id }
                                    if (existingDevice is Device) {
                                        existingDevice.updateState(ip)
                                    } else {
                                        device.updateState(ip)
                                        devices.update {
                                            val updatedDevices = it.toMutableList()
                                            updatedDevices.add(device)
                                            return@update updatedDevices
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }
    }
}