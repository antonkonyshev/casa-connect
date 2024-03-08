package name.antonkonyshev.home.data.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.repository.DeviceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : DeviceRepository {
    private val deviceDao by lazy { database.deviceDao() }

    override val allDevices: Flow<List<Device>> = deviceDao.getAllFlow().map {
        it.map { deviceModel -> deviceModel.toDevice() }
    }
    override val meteoDevices: Flow<List<Device>> =
        deviceDao.byServiceFlow(Device.METEO_SENSOR_SERVICE_TYPE).map {
            it.map { deviceModel -> deviceModel.toDevice() }
        }

    override fun updateStateOrCreate(device: DeviceModel) {
        if (deviceDao.updateStateById(device.id, device.ip!!) < 1) {
            deviceDao.insert(device)
        }
    }

    fun updateStateOrCreate(device: Device) {
        updateStateOrCreate(DeviceModel.fromDevice(device))
    }

    override fun updateAvailability(device: Device) {
        deviceDao.updateAvailabilityById(device.id, device.available)
    }

    override fun updateAllDevicesAvailability(available: Boolean) {
        deviceDao.updateAllDevicesAvailability(available)
    }

    override fun byService(service: String): List<Device> {
        return deviceDao.byService(service).map { it.toDevice() }
    }

    override fun byId(id: String): Device? {
        try {
            return deviceDao.byId(id).toDevice()
        } catch (_: Exception) {
        }
        return null
    }
}