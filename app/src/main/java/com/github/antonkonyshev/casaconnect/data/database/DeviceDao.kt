package com.github.antonkonyshev.casaconnect.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.net.InetAddress

@Dao
interface DeviceDao {
    @Query("select * from device order by ordering desc")
    fun getAll(): List<DeviceModel>

    @Query("select * from device order by ordering desc")
    fun getAllFlow(): Flow<List<DeviceModel>>

    @Query("select * from device where service = :service order by ordering desc")
    fun byService(service: String): List<DeviceModel>

    @Query("select * from device where service = :service order by ordering desc")
    fun byServiceFlow(service: String): Flow<List<DeviceModel>>

    @Query("select * from device where available = :available order by ordering desc")
    fun byAvailability(available: Boolean): List<DeviceModel>

    @Query("select * from device where id = :id limit 1")
    fun byId(id: String): DeviceModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: DeviceModel): Long

    @Query("update device set ip = :ip, available = :available, updated = strftime('%s', 'now') where id = :id")
    fun updateStateById(id: String, ip: InetAddress, available: Boolean? = true): Int

    @Query("update device set available = :available, updated = strftime('%s', 'now') where id = :id")
    fun updateAvailabilityById(id: String, available: Boolean): Int

    @Query("update device set available = :available")
    fun updateAllDevicesAvailability(available: Boolean)
}