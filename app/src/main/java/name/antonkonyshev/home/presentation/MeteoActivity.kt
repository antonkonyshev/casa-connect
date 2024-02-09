package name.antonkonyshev.home.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.ui.theme.HomeTheme

class MeteoActivity : BaseActivity() {
    private val viewModel: MeteoViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId: String? = getIntent().getStringExtra("deviceId")
        if (deviceId is String && deviceId.isNotEmpty()) {
            // TODO: Show selected in the foreground
        }

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.METEO,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    viewModel.uiState.collectAsState().value,
                    viewModel.getDevicesByServiceUseCase.getMeteoDevicesFlow().collectAsState(
                        initial = emptyList<DeviceModel>()
                    ).value,
                    viewModel.measurements,
                )
            }
        }
    }
}