package com.github.antonkonyshev.casaconnect.data.network

import com.github.antonkonyshev.casaconnect.data.database.DeviceModel
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import java.net.InetAddress

class NetworkDevice(
    val ip: InetAddress? = null,
) {
    fun getMainEndpointUrl(): String {
        return "http://${ip!!.hostAddress}/"
    }

    fun getServiceUrl(): String {
        return "http://${ip!!.hostAddress}/service"
    }

    fun getHistoryUrl(): String {
        return "http://${ip!!.hostAddress}/history"
    }

    fun getPreferenceUrl(): String {
        return "http://${ip!!.hostAddress}/settings"
    }

    companion object {
        fun fromDevice(device: Device): NetworkDevice = NetworkDevice(device.ip)

        fun fromDevice(device: DeviceModel): NetworkDevice = NetworkDevice(device.ip)
    }
}