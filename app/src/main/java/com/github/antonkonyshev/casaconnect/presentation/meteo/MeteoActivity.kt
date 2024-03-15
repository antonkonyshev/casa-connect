package com.github.antonkonyshev.casaconnect.presentation.meteo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.preference.PreferenceManager
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MeteoActivity : BaseActivity() {
    private lateinit var measurementTimer: Timer

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MeteoViewModel by viewModels()
        viewModel.observeMeasurement()
        measurementTimer = Timer()

        setContent {
            val windowSize = calculateWindowSizeClass(activity = this).widthSizeClass
            CasaConnectTheme {
                NavigationWrapper(
                    windowSize,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.METEO,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        MeteoScreen(
                            viewModel.getDevicesByServiceUseCase.getMeteoDevicesFlow()
                                .collectAsState(initial = emptyList()).value,
                            viewModel.measurements.mapValues { it.value.measurementFlow.collectAsState() },
                            viewModel.measurements.mapValues { it.value.historyFlow.collectAsState() },
                            uiState = viewModel.uiState.collectAsState().value,
                            windowSize = windowSize,
                            onDrawerClicked = onDrawerClicked,
                            onRefresh = { viewModel.observeMeasurement(true) }
                        )
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val viewModel: MeteoViewModel by viewModels()
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        val measurementUpdatePeriod: Long = 1000L * try {
            settings.getString("measurement_update_period", "15")!!.toLong()
        } catch (_: Exception) {
            15L
        }

        try {
            viewModel.measurementHistoryUpdatePeriod =
                settings.getString("measurement_history_update_period", "600")!!.toLong() * 1000L
        } catch (_: Exception) {
        }

        measurementTimer.scheduleAtFixedRate(measurementUpdatePeriod, measurementUpdatePeriod) {
            viewModel.observeMeasurement(true)
        }
    }

    override fun onStop() {
        super.onStop()
        measurementTimer.cancel()
    }
}