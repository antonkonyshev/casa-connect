package name.antonkonyshev.home.devices

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.os.Binder
import android.os.IBinder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.net.util.SubnetUtils
import java.net.Inet4Address
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class DiscoveryService : Service() {
    private val devicesStateUpdatePeriod = 60L  // seconds

    private val _devices = MutableStateFlow(emptyList<Device>())
    val devices = _devices.asStateFlow()

    private var scanning = false
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    inner class DiscoveryBinder : Binder() {
        fun getService() : DiscoveryService = this@DiscoveryService
    }

    private val binder = DiscoveryBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        discoverDevices()
        Timer().scheduleAtFixedRate(
            delay = devicesStateUpdatePeriod, period = devicesStateUpdatePeriod * 1000L
        ) { scope.launch { updateDevicesState() } }
    }

    fun discoverDevices() {
        scope.launch { discoverLocalDevices() }
    }

    suspend fun discoverLocalDevices() {
        if (scanning) { return }
        scanning = true

        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
            connectivityManager.activeNetwork
        )?.linkAddresses?.find{ la -> la.address is Inet4Address }
        if (linkAddress is LinkAddress) {
            val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
                it != linkAddress.address.hostAddress
            }
            _devices.value.forEach { it.available = false }

            // TODO: remove testing device
            _devices.update {
                val updatedDevices = it.toMutableList()
                updatedDevices.add(Device(
                    "testing-device-1", "meteo", "Outdoors",
                    listOf("temperature", "altitude", "pressure"),
                    Inet4Address.getByName("192.168.0.100"), false))
                return@update updatedDevices.toList()
            }
            // TODO: remove testing device
            _devices.update {
                val updatedDevices = it.toMutableList()
                updatedDevices.add(Device("testing-device-2", "meteo", "Kitchen",
                    listOf("temperature", "humidity", "pressure"),
                    Inet4Address.getByName("192.168.0.140"), true))
                return@update updatedDevices.toList()
            }

            ipRange.forEach { ipAddress ->
                val ip = Inet4Address.getByName(ipAddress)
                if (ip.isMulticastAddress()) {
                    return@forEach
                }
                withContext(Dispatchers.IO) {
                    if (ip.isReachable(1000)) {
                        try {
                            val response = OkHttpClient().newCall(
                                Request.Builder().url("http://" + ip.hostAddress + "/service")
                                    .build()
                            ).execute()
                            if (response.code == 200) {
                                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                                val device = moshi.adapter(Device::class.java)
                                    .fromJson(response.body!!.source())
                                if (device is Device) {
                                    val existingDevice = devices.value.find { it.id == device.id }
                                    if (existingDevice is Device) {
                                        existingDevice.updateState(ip)
                                    } else {
                                        device.updateState(ip)
                                        _devices.update {
                                            val updatedDevices = it.toMutableList()
                                            updatedDevices.add(device)
                                            return@update updatedDevices
                                        }
                                    }
                                }
                            }
                        } catch (err: Exception) {}
                    }
                }
            }
        }
        scanning = false
    }

    suspend fun updateDevicesState() {
        // TODO: implement
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
