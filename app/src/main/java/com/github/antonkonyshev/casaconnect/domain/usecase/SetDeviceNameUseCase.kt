package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import javax.inject.Inject

class SetDeviceNameUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(device: Device) {
        repository.updateDeviceName(device)
    }
}