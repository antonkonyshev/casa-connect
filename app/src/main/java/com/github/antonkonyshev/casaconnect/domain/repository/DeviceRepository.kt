package com.github.antonkonyshev.casaconnect.domain.repository

import kotlinx.coroutines.flow.Flow
import com.github.antonkonyshev.casaconnect.data.database.DeviceModel
import com.github.antonkonyshev.casaconnect.domain.entity.Device

interface DeviceRepository {

    val allDevices: Flow<List<Device>>
    val meteoDevices: Flow<List<Device>>

    fun byId(id: String): Device?
    fun byService(service: String): List<Device>
    fun updateAvailability(device: Device)
    fun updateStateOrCreate(device: DeviceModel)
    fun updateAllDevicesAvailability(available: Boolean)

}