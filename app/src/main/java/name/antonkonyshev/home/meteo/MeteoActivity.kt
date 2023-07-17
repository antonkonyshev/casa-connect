package name.antonkonyshev.home.meteo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import name.antonkonyshev.home.BaseActivity
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.NavigationDestinations
import name.antonkonyshev.home.NavigationWrapper
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.devices.DiscoveryService
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
                    viewModel.getApplication<HomeApplication>().deviceRepository
                        .meteoDevices.collectAsState(initial = emptyList<Device>()).value,
                )
            }
        }
    }
}