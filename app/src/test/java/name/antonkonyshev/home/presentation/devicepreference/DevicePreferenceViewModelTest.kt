package name.antonkonyshev.home.presentation.devicepreference

import android.content.Intent
import androidx.activity.viewModels
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.presentation.device.DevicesActivity
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
            val ctx = ApplicationProvider.getApplicationContext<HomeApplication>()
            val controller = Robolectric.buildActivity(
                DevicesActivity::class.java,
                Intent(ctx, DevicesActivity::class.java)
            )

            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.selectedDevice = device
            viewModel.getDevicePreferenceUseCase = spy(viewModel.getDevicePreferenceUseCase)
            doReturn(DevicePreference(device = device)).`when`(viewModel.getDevicePreferenceUseCase)
                .invoke(device)

            controller.setup()

            viewModel.prepareData { assertTrue(it) }
            verify(viewModel.getDevicePreferenceUseCase, timeout(5000L)).invoke(device)

            assertEquals(device.name, viewModel.deviceName)
            assertEquals(0, viewModel.highPollution)
            assertEquals(0, viewModel.minTemperature)
            assertEquals(0, viewModel.maxTemperature)
            assertEquals(0, viewModel.measurementPeriod)
            assertEquals(0, viewModel.historyLength)
            assertEquals(0, viewModel.historyRecordPeriod)
            assertEquals("", viewModel.wifiSsid)
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
            val ctx = ApplicationProvider.getApplicationContext<HomeApplication>()
            val controller = Robolectric.buildActivity(
                DevicesActivity::class.java,
                Intent(ctx, DevicesActivity::class.java)
            )

            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.setDevicePreferenceUseCase = spy(viewModel.setDevicePreferenceUseCase)
            viewModel.selectedDevice = device
            doReturn(true).`when`(viewModel.setDevicePreferenceUseCase).invoke(any())
            controller.setup()
            viewModel.highPollution = 1
            viewModel.minTemperature = 2
            viewModel.maxTemperature = 3
            viewModel.measurementPeriod = 4
            viewModel.historyLength = 5
            viewModel.historyRecordPeriod = 6
            viewModel.saveDevicePreference { assertTrue(it) }
            verify(viewModel.setDevicePreferenceUseCase, timeout(5000L)).invoke(argThat { preference: DevicePreference ->
                assertEquals(1, preference.highPollution)
                assertEquals(2, preference.minTemperature)
                assertEquals(3, preference.maxTemperature)
                assertEquals(4, preference.measurementPeriod)
                assertEquals(5, preference.historyLength)
                assertEquals(6, preference.historyRecordPeriod)
                return@argThat true
            })
        }
    }
}