package name.antonkonyshev.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DeviceRepository

class GetDevicesByServiceUseCase(private val repository: DeviceRepository) {

    fun getAllDevicesFlow(): Flow<List<DeviceModel>> {
        return repository.allDevices
    }

    fun getMeteoDevicesFlow(): Flow<List<DeviceModel>> {
        return repository.meteoDevices
    }

    fun getMeteoDevicesList(): List<DeviceModel> {
        return repository.byService("meteo")
    }

}