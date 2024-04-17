package com.github.antonkonyshev.casaconnect.data.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
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

    override fun updateDeviceName(device: Device) {
        deviceDao.updateDeviceName(device.id, device.name)
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