package name.antonkonyshev.home.presentation.devicepreference

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.presentation.NavigationDestinations
import name.antonkonyshev.home.presentation.NavigationWrapper
import name.antonkonyshev.home.ui.theme.HomeTheme

class DevicePreferenceActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: DevicePreferenceViewModel by viewModels()

        val deviceId: String? = intent.getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            viewModel.selectDevice(deviceId)
        }

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.DEVICE_PREFERENCES,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    viewModel.uiState.collectAsState().value,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        DevicePreferenceScreen(
                            viewModel,
                            onDrawerClicked = onDrawerClicked,
                            onSave = { viewModel.saveDevicePreference(this) }
                        )
                    }
                )
            }
        }
    }
}
