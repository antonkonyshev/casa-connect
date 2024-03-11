package name.antonkonyshev.home.presentation.devicepreference

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.presentation.NavigationDestinations
import name.antonkonyshev.home.presentation.NavigationWrapper
import name.antonkonyshev.home.ui.theme.HomeTheme
import java.net.InetAddress

class DevicePreferenceActivity : BaseActivity() {
    val viewModel: DevicePreferenceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.DEVICE_PREFERENCES,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        DevicePreferenceScreen(
                            viewModel,
                            onDrawerClicked = { finish() },
                            onSave = ::saveDevicePreference,
                            viewModel.uiState.collectAsState().value
                        )
                    }
                )
            }
        }

        val deviceId: String? = intent.getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            selectDevice(deviceId)
        }
    }

    fun selectDevice(deviceId: String) {
        if (viewModel.selectedDevice == null) {
            viewModel.onLoading()
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                viewModel.selectedDevice = viewModel.getDeviceByIdUseCase(deviceId)
                if (
                    viewModel.selectedDevice != null && viewModel.selectedDevice?.ip is InetAddress
                ) {
                    val preference =
                        viewModel.getDevicePreferenceUseCase(viewModel.selectedDevice!!)
                    if (preference != null) {
                        viewModel.deviceName = viewModel.selectedDevice?.name ?: ""
                        viewModel.highPollution = preference.highPollution
                        viewModel.minTemperature = preference.minTemperature
                        viewModel.maxTemperature = preference.maxTemperature
                        viewModel.measurementPeriod = preference.measurementPeriod
                        viewModel.timeSyncPeriod = preference.timeSyncPeriod
                        viewModel.historyLength = preference.historyLength
                        viewModel.historyRecordPeriod = preference.historyRecordPeriod
                        viewModel.wifiSsid = preference.wifiSsid
                        viewModel.onLoaded()
                    } else {
                        viewModel.onLoaded()
                        finish()
                    }
                }
            }
        }
    }

    fun saveDevicePreference() {
        if (viewModel.selectedDevice != null) {
            viewModel.onLoading()
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                val result = viewModel.setDevicePreferenceUseCase(
                    DevicePreference(
                        viewModel.highPollution, viewModel.minTemperature, viewModel.maxTemperature,
                        viewModel.measurementPeriod, viewModel.timeSyncPeriod,
                        viewModel.historyLength, viewModel.historyRecordPeriod, "",
                        viewModel.selectedDevice
                    )
                )
                viewModel.onLoaded()
                if (result) {
                    finish()
                }
            }
        }
    }
}
