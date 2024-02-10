package name.antonkonyshev.home.presentation.devicepreference

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.ui.theme.HomeTheme

class DevicePreferenceActivity : BaseActivity() {
    private val viewModel: DevicePreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId: String? = intent.getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            viewModel.selectDevice(deviceId)
        }

        setContent {
            HomeTheme {
                DevicePreferenceScreen(
                    viewModel.preference.collectAsState().value
                )
            }
        }
    }
}
