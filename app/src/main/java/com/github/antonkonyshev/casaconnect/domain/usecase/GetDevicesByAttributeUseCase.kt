package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DeviceType
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDevicesByAttributeUseCase @Inject constructor(
    private val repository: DeviceRepository
) {

    fun getAllDevicesFlow(): Flow<List<Device>> {
        return repository.allDevices
    }

    fun getMeteoDevicesFlow(): Flow<List<Device>> {
        return repository.meteoDevices
    }

    fun getMeteoDevicesList(): List<Device> {
        return repository.byDeviceType(DeviceType.MeteoDeviceType)
    }

    fun getDoorDevicesList(): List<Device> {
        return repository.byDeviceType(DeviceType.DoorDeviceType)
    }

    fun getById(id: String): Device? {
        return repository.byId(id)
    }
}