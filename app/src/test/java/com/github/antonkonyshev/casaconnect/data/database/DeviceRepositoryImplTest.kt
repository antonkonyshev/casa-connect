package com.github.antonkonyshev.casaconnect.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.casaconnect.data.database.AppDatabase
import com.github.antonkonyshev.casaconnect.data.database.DeviceModel
import com.github.antonkonyshev.casaconnect.data.database.DeviceRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DeviceType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.Inet4Address

@RunWith(RobolectricTestRunner::class)
class DeviceRepositoryImplTest {
    private lateinit var repository: DeviceRepositoryImpl
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        runBlocking {
            database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), AppDatabase::class.java
            ).allowMainThreadQueries().build()
            repository = DeviceRepositoryImpl(database)
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            database.close()
        }
    }

    @Test
    fun getAllDevices() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), true
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "meteo", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName("192.168.0.102"), false
                )
            )
            val values = repository.allDevices.first().sortedBy { it.id }
            assertEquals(values.size, 2)
            assertEquals(values[0].ip!!.hostAddress, "192.168.0.102")
            assertEquals(values[0].id, "kitchen-1")
            assertEquals(values[0].service, "meteo")
            assertFalse(values[0].available)
            assertEquals(values[1].ip!!.hostAddress, "192.168.0.101")
            assertEquals(values[1].id, "room-1")
            assertEquals(values[1].service, "meteo")
            assertTrue(values[1].available)
        }
    }

    @Test
    fun getMeteoDevices() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), true
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "light", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName("192.168.0.102"), false
                )
            )
            val values = repository.meteoDevices.first()
            assertEquals(values.size, 1)
            assertEquals(values[0].ip!!.hostAddress, "192.168.0.101")
            assertEquals(values[0].id, "room-1")
            assertEquals(values[0].service, "meteo")
        }
    }

    @Test
    fun updateStateOrCreate() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), false
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Bedroom",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.111"), true
                )
            )
            val values = repository.meteoDevices.first()
            assertEquals(values.size, 1)
            assertEquals(values[0].ip!!.hostAddress, "192.168.0.111")
            assertEquals(values[0].id, "room-1")
            assertTrue(values[0].available)
        }
    }

    @Test
    fun updateAvailability() {
        runBlocking {
            val device = Device(
                "room-1", "meteo", "Room",
                listOf("temperature", "pollution"),
                Inet4Address.getByName("192.168.0.101"), true
            )
            repository.updateStateOrCreate(device)
            device.available = false
            repository.updateAvailability(device)
            val values = repository.meteoDevices.first()
            assertEquals(values.size, 1)
            assertEquals(values[0].ip!!.hostAddress, "192.168.0.101")
            assertEquals(values[0].id, "room-1")
            assertFalse(values[0].available)
        }
    }

    @Test
    fun updateAllDevicesAvailability() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), true
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "meteo", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName("192.168.0.102"), true
                )
            )
            repository.updateAllDevicesAvailability(false)
            val values = repository.allDevices.first().sortedBy { it.id }
            assertEquals(values.size, 2)
            assertFalse(values[0].available)
            assertFalse(values[1].available)
        }
    }

    @Test
    fun byDeviceType() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), true
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "door", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName("192.168.0.102"), true
                )
            )
            val values = repository.byDeviceType(DeviceType.DoorDeviceType)
            assertEquals(values.size, 1)
            assertEquals(values[0].id, "kitchen-1")
            assertEquals(values[0].ip!!.hostAddress, "192.168.0.102")
        }
    }

    @Test
    fun byId() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName("192.168.0.101"), true
                )
            )
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "light", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName("192.168.0.102"), true
                )
            )
            var device = repository.byId("room-1")!!
            assertNotNull(device)
            assertEquals(device.id, "room-1")
            assertEquals(device.ip!!.hostAddress, "192.168.0.101")
            assertEquals(device.service, "meteo")
            device = repository.byId("kitchen-1")!!
            assertNotNull(device)
            assertEquals(device.id, "kitchen-1")
            assertEquals(device.ip!!.hostAddress, "192.168.0.102")
            assertEquals(device.service, "light")
            val notExistingDevice = repository.byId("testing-1")
            assertNull(notExistingDevice)
        }
    }
}