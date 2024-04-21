package com.github.antonkonyshev.casaconnect.domain.repository

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import java.net.InetAddress

interface DiscoveryService {
    suspend fun discoverDevices()
    suspend fun retrieveServiceInfo(ip: InetAddress) : Device?
}