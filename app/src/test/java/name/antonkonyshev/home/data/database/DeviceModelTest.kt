package name.antonkonyshev.home.data.database

import name.antonkonyshev.home.domain.entity.Device
import org.junit.Assert.*

import org.junit.Test
import java.net.Inet4Address

class DeviceModelTest {

    @Test
    fun toDevice() {
        val deviceModel = DeviceModel(
            "room-1", "meteo", "Room",
            listOf("temperature", "pollution"),
            Inet4Address.getByName("192.168.0.101"), true
        )
        val device = deviceModel.toDevice()
        assertNotEquals(device, deviceModel)
        assertEquals(device.id, deviceModel.id)
        assertEquals(device.ip!!.hostAddress, deviceModel.ip!!.hostAddress)
        assertEquals(device.service, deviceModel.service)
        assertEquals(device.name, deviceModel.name)
        assertEquals(device.sensors, deviceModel.sensors)
        assertEquals(device.available, deviceModel.available)
    }

    @Test
    fun fromDevice() {
        val device = Device(
            "room-1", "meteo", "Room",
            listOf("temperature", "pollution"),
            Inet4Address.getByName("192.168.0.101"), true
        )
        val deviceModel = DeviceModel.fromDevice(device)
        assertNotEquals(device, deviceModel)
        assertEquals(device.id, deviceModel.id)
        assertEquals(device.ip!!.hostAddress, deviceModel.ip!!.hostAddress)
        assertEquals(device.service, deviceModel.service)
        assertEquals(device.name, deviceModel.name)
        assertEquals(device.sensors, deviceModel.sensors)
        assertEquals(device.available, deviceModel.available)
    }
}