package name.antonkonyshev.home.meteo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.BaseActivity
import name.antonkonyshev.home.HomeViewModel
import name.antonkonyshev.home.NavigationDestinations
import name.antonkonyshev.home.NavigationWrapper
import name.antonkonyshev.home.ui.theme.HomeTheme

class MeteoActivity : BaseActivity() {
    private val viewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeTheme {
                NavigationWrapper(
                    calculateWindowSizeClass(activity = this).widthSizeClass,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.METEO,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    viewModel.loading.collectAsState().value,
                    viewModel.devices.collectAsState().value,
                )
            }
        }
    }
}