package name.antonkonyshev.home.data.network

import name.antonkonyshev.home.domain.entity.Device
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.net.Inet4Address

class NetworkDeviceTest {
    private var networkDevice: NetworkDevice? = null

    @Before
    fun setUp() {
        networkDevice = NetworkDevice(Inet4Address.getByName("192.168.0.101"))
    }

    @After
    fun tearDown() {
        networkDevice = null
    }

    @Test
    fun getMeasurementUrl() {
        assertEquals(networkDevice!!.getMeasurementUrl(), "http://192.168.0.101/")
    }

    @Test
    fun getMeasurementUrl_withoutIp() {
        networkDevice = NetworkDevice(null)
        assertThrows(NullPointerException::class.java) {
            networkDevice!!.getMeasurementUrl()
        }
    }

    @Test
    fun getHistoryUrl() {
        assertEquals(networkDevice!!.getHistoryUrl(), "http://192.168.0.101/history")
    }

    @Test
    fun getPreferenceUrl() {
        assertEquals(networkDevice!!.getPreferenceUrl(), "http://192.168.0.101/settings")
    }

    @Test
    fun getServiceUrl() {
        assertEquals(networkDevice!!.getServiceUrl(), "http://192.168.0.101/service")
    }

    @Test
    fun getIp() {
        assertEquals(networkDevice!!.ip!!.hostAddress, "192.168.0.101")
    }

    @Test
    fun fromDevice() {
        tearDown()
        networkDevice = NetworkDevice.fromDevice(
            Device(
                "room-1", "meteo", "Room",
                listOf("temperature", "altitude", "pollution"),
                Inet4Address.getByName("192.168.0.101"), true
            )
        )
        assertEquals(networkDevice!!.getMeasurementUrl(), "http://192.168.0.101/")
    }
}