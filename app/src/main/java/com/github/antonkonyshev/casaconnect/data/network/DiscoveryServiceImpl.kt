package com.github.antonkonyshev.casaconnect.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.data.database.DeviceModel
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import com.github.antonkonyshev.casaconnect.domain.repository.DiscoveryService
import org.apache.commons.net.util.SubnetUtils
import retrofit2.http.GET
import retrofit2.http.Url
import java.net.Inet4Address
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface DeviceModelSchema {
    @GET
    suspend fun getServiceInfo(@Url url: String): DeviceModel
}

@Singleton
class DiscoveryServiceImpl @Inject constructor(
    private val schema: DeviceModelSchema,
    private val deviceRepository: DeviceRepository,
) : DiscoveryService {
    private var scanning = false

    override suspend fun discoverDevices() {
        if (scanning) {
            return
        }
        scanning = true
        try {
            val linkAddress: LinkAddress? = linkAddress()
            deviceRepository.updateAllDevicesAvailability(false)
            if (linkAddress is LinkAddress) {
                val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
                    it != linkAddress.address.hostAddress
                }
                ipRange.forEach { addr ->
                    CoroutineScope(Dispatchers.IO).launch {
                        checkIpAddress(addr)
                    }
                }
            }
        } catch (_: Exception) {
        }
        scanning = false
    }

    suspend fun checkIpAddress(ipAddress: String) {
        val ip = Inet4Address.getByName(ipAddress)
        if (ip.isMulticastAddress) {
            return
        }
        if (ip.isReachable(1000)) {
            retrieveServiceInfo(ip)
        }
    }

    override suspend fun retrieveServiceInfo(ip: InetAddress): Device? {
        try {
            val device = schema.getServiceInfo(NetworkDevice(ip).getServiceUrl())
            device.ip = ip
            deviceRepository.updateStateOrCreate(device)
            return device.toDevice()
        } catch (err: Exception) {
            Log.d("Discover", err.toString())
        }
        return null
    }

    private fun linkAddress(): LinkAddress? {
        val connectivityManager = CasaConnectApplication.instance.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return connectivityManager.getLinkProperties(
            connectivityManager.activeNetwork
        )?.linkAddresses?.find { la -> la.address is Inet4Address }
    }
}