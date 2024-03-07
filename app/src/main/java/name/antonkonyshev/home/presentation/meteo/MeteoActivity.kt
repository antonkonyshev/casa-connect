package name.antonkonyshev.home.presentation.meteo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.presentation.NavigationDestinations
import name.antonkonyshev.home.presentation.NavigationWrapper
import name.antonkonyshev.home.ui.theme.HomeTheme

class MeteoActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MeteoViewModel by viewModels()

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.METEO,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    viewModel.uiState.collectAsState().value,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        MeteoScreen(
                            viewModel.getDevicesByServiceUseCase.getMeteoDevicesFlow().collectAsState(initial = emptyList()).value,
                            viewModel.measurements.mapValues { it.value.measurementFlow.collectAsState() },
                            viewModel.measurements.mapValues { it.value.historyFlow.collectAsState() },
                            onDrawerClicked = onDrawerClicked
                        )
                    }
                )
            }
        }
    }
}