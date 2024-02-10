package name.antonkonyshev.home.domain.repository

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.domain.entity.Device

interface DeviceRepository {

    val allDevices: Flow<List<Device>>
    val meteoDevices: Flow<List<Device>>

    fun byId(id: String): Device?
    fun byService(service: String): List<Device>
    fun updateAvailability(device: Device)

}