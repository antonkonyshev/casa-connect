package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import com.github.antonkonyshev.casaconnect.domain.repository.DiscoveryService
import javax.inject.Inject

class UpdateDeviceAvailabilityUseCase @Inject constructor(
    private val repository: DeviceRepository,
    private val service: DiscoveryService
) {

    operator fun invoke(device: Device) {
        repository.updateAvailability(device)
    }

    suspend fun checkDeviceAvailability(device: Device): Device? {
        return device.ip?.let { service.retrieveServiceInfo(it) }
    }

}