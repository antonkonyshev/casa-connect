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
    private val viewModel: DevicePreferenceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId: String? = intent.getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            viewModel.selectDevice(deviceId)
        }

        Log.d("DevicePreferenceActivity.onCreated", viewModel.preference.value.toString())

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
                            viewModel.preference.collectAsState().value,
                            onDrawerClicked = onDrawerClicked,
                            onSave = { onSave() }
                        )
                    }
                )
            }
        }
    }

    fun onSave() {
        Log.d("DevicePreferenceActivity.onSave", viewModel.preference.value.toString())
    }
}
