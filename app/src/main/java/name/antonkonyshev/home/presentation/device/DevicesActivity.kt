package name.antonkonyshev.home.presentation.device

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.presentation.NavigationDestinations
import name.antonkonyshev.home.presentation.NavigationWrapper
import name.antonkonyshev.home.presentation.devicepreference.DevicePreferenceActivity
import name.antonkonyshev.home.ui.theme.HomeTheme

// TODO: Storing info about local services and checking availability of them on start up or resume
class DevicesActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: DevicesViewModel by viewModels()

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.DEVICES,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        DevicesScreen(
                            viewModel.getDevicesByServiceUseCase.getAllDevicesFlow()
                                .collectAsState(initial = emptyList()).value,
                            onDiscoverDevicesClicked = viewModel::discoverDevices,
                            onDeviceClicked = ::onDeviceClicked,
                            onDrawerClicked = onDrawerClicked,
                            uiState = viewModel.uiState.collectAsState().value
                        )
                    }
                )
            }
        }
    }

    private fun onDeviceClicked(device: Device) {
        startActivity(Intent(this, DevicePreferenceActivity::class.java)
            .apply { putExtra("deviceId", device.id) })
    }
}
