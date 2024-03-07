package name.antonkonyshev.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.repository.DeviceRepository
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