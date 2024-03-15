package com.github.antonkonyshev.casaconnect.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import javax.inject.Inject

class GetDevicesByServiceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {

    fun getAllDevicesFlow(): Flow<List<Device>> {
        return repository.allDevices
    }

    fun getMeteoDevicesFlow(): Flow<List<Device>> {
        return repository.meteoDevices
    }

    fun getMeteoDevicesList(): List<Device> {
        return repository.byService(Device.METEO_SENSOR_SERVICE_TYPE)
    }

}