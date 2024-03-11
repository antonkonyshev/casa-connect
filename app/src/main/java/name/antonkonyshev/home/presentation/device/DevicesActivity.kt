package name.antonkonyshev.home.presentation.device

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
                    viewModel.uiState.collectAsState().value,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        DevicesScreen(
                            viewModel.getDevicesByServiceUseCase.getAllDevicesFlow()
                                .collectAsState(initial = emptyList()).value,
                            onDiscoverDevicesClicked = {
                                viewModel.viewModelScope.async(Dispatchers.IO) {
                                    viewModel.discoverDevicesInLocalNetworkUseCase()
                                }
                            },
                            onDeviceClicked = ::onDeviceClicked,
                            onDrawerClicked = onDrawerClicked
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
