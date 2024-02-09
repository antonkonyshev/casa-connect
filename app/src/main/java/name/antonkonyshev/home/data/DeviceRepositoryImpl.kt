package name.antonkonyshev.home.data

import kotlinx.coroutines.flow.Flow
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.data.database.AppDatabase
import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DeviceRepository

object DeviceRepositoryImpl : DeviceRepository {
    private val database by lazy { AppDatabase.instance(HomeApplication.instance) }
    private val deviceDao by lazy { database.deviceDao() }

    override val allDevices: Flow<List<DeviceModel>> = deviceDao.getAllFlow()
    override val meteoDevices: Flow<List<DeviceModel>> = deviceDao.byServiceFlow("meteo")

    fun updateStateOrCreate(device: DeviceModel) {
        if (deviceDao.updateStateById(device.id, device.ip!!) < 1) {
            deviceDao.insert(device)
        }
    }

    override fun updateAvailability(device: DeviceModel) {
        deviceDao.updateAvailabilityById(device.id, device.available)
    }

    fun updateAllDevicesAvailability(available: Boolean) {
        deviceDao.updateAllDevicesAvailability(available)
    }

    fun getAll(): List<DeviceModel> {
        return deviceDao.getAll()
    }

    override fun byService(service: String): List<DeviceModel> {
        return deviceDao.byService(service)
    }

    override fun byId(id: String): DeviceModel? {
        return deviceDao.byId(id)
    }
}