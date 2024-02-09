package name.antonkonyshev.home.domain.repository

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.data.database.DeviceModel

interface DeviceRepository {

    val allDevices: Flow<List<DeviceModel>>
    val meteoDevices: Flow<List<DeviceModel>>

    fun byId(id: String): DeviceModel?
    fun byService(service: String): List<DeviceModel>
    fun updateAvailability(device: DeviceModel)

}