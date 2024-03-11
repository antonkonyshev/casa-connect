package name.antonkonyshev.home.presentation.devicepreference

import android.content.Intent
import androidx.activity.viewModels
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.net.Inet4Address

@RunWith(RobolectricTestRunner::class)
class DevicePreferenceActivityTest {

    @Test
    fun onCreate() {
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
            val controller = Robolectric.buildActivity(DevicePreferenceActivity::class.java,
                Intent(ctx, DevicePreferenceActivity::class.java).apply {
                    putExtra("deviceId", device.id)
                })
            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.getDeviceByIdUseCase = spy(viewModel.getDeviceByIdUseCase)
            doReturn(device).`when`(viewModel.getDeviceByIdUseCase).invoke(device.id)
            viewModel.getDevicePreferenceUseCase = spy(viewModel.getDevicePreferenceUseCase)
            doReturn(DevicePreference(device = device)).`when`(viewModel.getDevicePreferenceUseCase)
                .invoke(device)
            controller.setup()
            assertEquals(viewModel.selectedDevice!!.id, device.id)
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
            val controller = Robolectric.buildActivity(DevicePreferenceActivity::class.java,
                Intent(ctx, DevicePreferenceActivity::class.java).apply {
                    putExtra("deviceId", device.id)
                })
            val viewModel = controller.get().viewModels<DevicePreferenceViewModel>().value
            viewModel.getDeviceByIdUseCase = spy(viewModel.getDeviceByIdUseCase)
            doReturn(device).`when`(viewModel.getDeviceByIdUseCase).invoke(device.id)
            viewModel.getDevicePreferenceUseCase = spy(viewModel.getDevicePreferenceUseCase)
            doReturn(DevicePreference(device = device)).`when`(viewModel.getDevicePreferenceUseCase)
                .invoke(device)
            viewModel.setDevicePreferenceUseCase = spy(viewModel.setDevicePreferenceUseCase)
            doReturn(true).`when`(viewModel.setDevicePreferenceUseCase).invoke(any())
            controller.setup()
            viewModel.highPollution = 1
            viewModel.minTemperature = 2
            viewModel.maxTemperature = 3
            viewModel.measurementPeriod = 4
            viewModel.historyLength = 5
            viewModel.historyRecordPeriod = 6
            controller.get().saveDevicePreference()
            verify(viewModel.setDevicePreferenceUseCase).invoke(argThat { preference: DevicePreference ->
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