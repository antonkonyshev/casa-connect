package com.github.antonkonyshev.casaconnect.presentation.devicepreference

import android.content.Intent
import androidx.activity.viewModels
import androidx.test.core.app.ApplicationProvider
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.presentation.common.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.net.Inet4Address

@RunWith(RobolectricTestRunner::class)
class DevicePreferenceViewModelTest {

    @Test
    fun onRetrieveDevicePreference() {
        runBlocking {
            val device = Device(
                "room-1",
                "meteo",
                "Room",
                listOf("temperature", "pressure", "pollution"),
                Inet4Address.getByName("192.168.0.101"),
                true
            )
            val ctx = ApplicationProvider.getApplicationContext<CasaConnectApplication>()
            val controller = Robolectric.buildActivity(
                MainActivity::class.java,
                Intent(ctx, MainActivity::class.java)
            )

            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.editableDevice = device
            viewModel.getDevicePreferenceUseCase = spy(viewModel.getDevicePreferenceUseCase)
            doReturn(DevicePreference(device = device)).`when`(viewModel.getDevicePreferenceUseCase)
                .invoke(device)

            controller.setup()

            viewModel.prepareData { assertTrue(it) }
            verify(viewModel.getDevicePreferenceUseCase, timeout(5000L)).invoke(device)

            assertEquals(device.name, viewModel.preference.value!!.device!!.name)
            assertEquals(0, viewModel.preference.value!!.highPollution)
            assertEquals(0, viewModel.preference.value!!.minTemperature)
            assertEquals(0, viewModel.preference.value!!.maxTemperature)
            assertEquals(0, viewModel.preference.value!!.measurementPeriod)
            assertEquals(0, viewModel.preference.value!!.historyLength)
            assertEquals(0, viewModel.preference.value!!.historyRecordPeriod)
            assertEquals("", viewModel.preference.value!!.wifiSsid)
        }
    }

    @Test
    fun saveDevicePreference() {
        runBlocking {
            val device = Device(
                "room-1",
                "meteo",
                "Room",
                listOf("temperature", "pressure", "pollution"),
                Inet4Address.getByName("192.168.0.101"),
                true
            )
            val preference = DevicePreference(
                20, 14, 24,
                15, 1800, 50, 1800,
                "", device
            )
            val ctx = ApplicationProvider.getApplicationContext<CasaConnectApplication>()
            val controller = Robolectric.buildActivity(
                MainActivity::class.java,
                Intent(ctx, MainActivity::class.java)
            )

            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.getDevicePreferenceUseCase = spy(viewModel.getDevicePreferenceUseCase)
            viewModel.setDevicePreferenceUseCase = spy(viewModel.setDevicePreferenceUseCase)
            viewModel.setDeviceNameUseCase = spy(viewModel.setDeviceNameUseCase)
            viewModel.editableDevice = device
            doReturn(preference).`when`(viewModel.getDevicePreferenceUseCase).invoke(any())
            doReturn(true).`when`(viewModel.setDevicePreferenceUseCase).invoke(any())
            controller.setup()
            viewModel.prepareData { assertTrue(it) }
            delay(100L)
            viewModel.preference.value!!.highPollution = 1
            viewModel.preference.value!!.minTemperature = 2
            viewModel.preference.value!!.maxTemperature = 3
            viewModel.preference.value!!.measurementPeriod = 4
            viewModel.preference.value!!.historyLength = 5
            viewModel.preference.value!!.historyRecordPeriod = 6
            viewModel.preference.value!!.device!!.name = "Test-Room"
            viewModel.saveDevicePreference { assertTrue(it) }
            verify(
                viewModel.setDevicePreferenceUseCase,
                timeout(5000L)
            ).invoke(argThat { preferenceArg: DevicePreference ->
                assertEquals(1, preferenceArg.highPollution)
                assertEquals(2, preferenceArg.minTemperature)
                assertEquals(3, preferenceArg.maxTemperature)
                assertEquals(4, preferenceArg.measurementPeriod)
                assertEquals(5, preferenceArg.historyLength)
                assertEquals(6, preferenceArg.historyRecordPeriod)
                return@argThat true
            })
            verify(
                viewModel.setDeviceNameUseCase,
                timeout(5000L)
            ).invoke(argThat { deviceArg: Device ->
                assertEquals("Test-Room", deviceArg.name)
                return@argThat true
            })
        }
    }
}