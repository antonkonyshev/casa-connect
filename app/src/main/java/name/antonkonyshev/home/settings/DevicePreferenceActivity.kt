package name.antonkonyshev.home.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import name.antonkonyshev.home.BaseActivity
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.NavigationDestinations
import name.antonkonyshev.home.NavigationWrapper
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.meteo.DeviceMeasurement
import name.antonkonyshev.home.ui.theme.HomeTheme

class DevicePreferenceActivity : BaseActivity() {
    private val viewModel: DevicePreferenceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId: String? = getIntent().getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            viewModel.viewModelScope.async(Dispatchers.IO) {
                val device = viewModel.getApplication<HomeApplication>()
                    .deviceRepository.byId(deviceId)
            }
        }

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.DEVICE_SETTINGS,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    viewModel.uiState.collectAsState().value,
                    emptyList<Device>(),
                    HashMap<String, DeviceMeasurement>(),
                )
            }
        }
    }
}
