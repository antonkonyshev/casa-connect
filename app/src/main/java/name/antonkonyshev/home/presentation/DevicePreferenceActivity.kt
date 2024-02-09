package name.antonkonyshev.home.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.ui.theme.HomeTheme

class DevicePreferenceActivity : BaseActivity() {
    private val viewModel: DevicePreferenceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId: String? = getIntent().getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            viewModel.selectDevice(deviceId)
        }

        setContent {
            HomeTheme {
                DevicePreferenceScreen(
                    viewModel.uiState.collectAsState().value,
                    viewModel.preference.collectAsState().value
                )
            }
        }
    }
}
