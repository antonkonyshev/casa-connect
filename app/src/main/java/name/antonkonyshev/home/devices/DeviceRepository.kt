package name.antonkonyshev.home.devices

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.DeviceDao

class DeviceRepository(val deviceDao: DeviceDao) {
    val allDevices: Flow<List<Device>> = deviceDao.getAllFlow()
    val meteoDevices: Flow<List<Device>> = deviceDao.byServiceFlow("meteo")

    fun updateStateOrCreate(device: Device) {
        if (deviceDao.updateStateById(device.id, device.ip!!) < 1) {
            deviceDao.insert(device)
        }
    }

    fun updateAvailability(device: Device) {
        deviceDao.updateAvailabilityById(device.id, device.available)
    }

    fun updateAllDevicesAvailability(available: Boolean) {
        deviceDao.updateAllDevicesAvailability(available)
    }

    fun getAll(): List<Device> {
        return deviceDao.getAll()
    }

    fun byService(service: String): List<Device> {
        return deviceDao.byService(service)
    }

    fun byId(id: String): Device? {
        return deviceDao.byId(id)
    }
}