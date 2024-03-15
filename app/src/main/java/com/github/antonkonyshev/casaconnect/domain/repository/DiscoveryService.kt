package com.github.antonkonyshev.casaconnect.domain.repository

interface DiscoveryService {
    suspend fun discoverDevices()
}