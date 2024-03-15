package com.github.antonkonyshev.casaconnect.data.network

import com.github.antonkonyshev.casaconnect.data.network.DevicePreferenceModel
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import org.junit.Assert.*

import org.junit.Test
import java.net.Inet4Address

class DevicePreferenceModelTest {

    @Test
    fun toDevicePreference() {
        val device = Device(
            "room-1", "meteo", "Room", listOf("temperature"),
            Inet4Address.getByName("192.168.0.101"), true
        )
        val model = DevicePreferenceModel(
            1, 2, 3, 4,
            5, 6, 7, "testing", device
        )
        val preference = model.toDevicePreference()
        assertEquals(model.highPollution, preference.highPollution)
        assertEquals(model.minTemperature, preference.minTemperature)
        assertEquals(model.maxTemperature, preference.maxTemperature)
        assertEquals(model.measurementPeriod, preference.measurementPeriod)
        assertEquals(model.timeSyncPeriod, preference.timeSyncPeriod)
        assertEquals(model.historyLength, preference.historyLength)
        assertEquals(model.historyRecordPeriod, preference.historyRecordPeriod)
        assertEquals(model.historyLength, preference.historyLength)
        assertEquals(model.device!!.id, preference.device!!.id)
        assertEquals(model.device!!.ip!!.hostAddress, preference.device!!.ip!!.hostAddress)
    }

    @Test
    fun fromDevicePreference() {
        val device = Device(
            "room-1", "meteo", "Room", listOf("temperature"),
            Inet4Address.getByName("192.168.0.101"), true
        )
        val preference = DevicePreference(
            1, 2, 3, 4,
            5, 6, 7, "testing", device
        )
        val model = DevicePreferenceModel.fromDevicePreference(preference)
        assertEquals(model.highPollution, preference.highPollution)
        assertEquals(model.minTemperature, preference.minTemperature)
        assertEquals(model.maxTemperature, preference.maxTemperature)
        assertEquals(model.measurementPeriod, preference.measurementPeriod)
        assertEquals(model.timeSyncPeriod, preference.timeSyncPeriod)
        assertEquals(model.historyLength, preference.historyLength)
        assertEquals(model.historyRecordPeriod, preference.historyRecordPeriod)
        assertEquals(model.historyLength, preference.historyLength)
        assertEquals(model.device!!.id, preference.device!!.id)
        assertEquals(model.device!!.ip!!.hostAddress, preference.device!!.ip!!.hostAddress)
    }
}