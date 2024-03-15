package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.repository.DiscoveryService
import javax.inject.Inject

class DiscoverDevicesInLocalNetworkUseCase @Inject constructor(
    private val service: DiscoveryService
) {
    suspend operator fun invoke() {
        service.discoverDevices()
    }
}