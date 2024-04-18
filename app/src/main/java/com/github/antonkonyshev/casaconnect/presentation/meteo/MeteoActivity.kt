package com.github.antonkonyshev.casaconnect.presentation.meteo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.preference.PreferenceManager
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MeteoActivity : BaseActivity() {
    private var measurementTimer: Timer? = null

    override val viewModel: MeteoViewModel by viewModels()
    override var header: String = ""
    override val navigationDestination: String = NavigationDestinations.METEO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        header = resources.getString(R.string.meteostation)

        viewModel.observeMeasurement()

        setContent {
            CasaConnectTheme {
                NavigationWrapper {
                    MeteoScreen(
                        viewModel.getDevicesByServiceUseCase.getMeteoDevicesFlow()
                            .collectAsStateWithLifecycle(initialValue = emptyList()).value,
                        viewModel.measurements.mapValues { it.value.measurementFlow.collectAsStateWithLifecycle() },
                        viewModel.measurements.mapValues { it.value.historyFlow.collectAsStateWithLifecycle() },
                        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                        onRefresh = ::onMeasurementsRefresh
                    )
                }
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

        if (measurementTimer == null) {
            measurementTimer = Timer()
        }
        measurementTimer?.scheduleAtFixedRate(measurementUpdatePeriod, measurementUpdatePeriod) {
            viewModel.observeMeasurement(true)
        }
    }

    override fun onStop() {
        super.onStop()
        measurementTimer?.cancel()
    }

    fun onMeasurementsRefresh() {
        viewModel.observeMeasurement(true)
    }
}