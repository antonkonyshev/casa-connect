package name.antonkonyshev.home.meteo

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.BaseViewModel
import name.antonkonyshev.home.HomeApplication
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MeteoViewModel(application: Application) : BaseViewModel(application) {
    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 15L  // seconds

    private val _measurement = MutableStateFlow(Measurement())
    val measurement = _measurement.asStateFlow()

    private val _history = MutableStateFlow(emptyList<Measurement>())
    val history = _history.asStateFlow()

    init {
        observeMeasurement()
        Timer().scheduleAtFixedRate(periodicalMeasurementUpdate, periodicalMeasurementUpdate * 1000L) {
            observeMeasurement(true)
        }
    }

    fun observeMeasurement(silent: Boolean = false) {
        if (uiState.value.scanning) { return }
        if (silent) {
            _uiState.update { it.copy(scanning = true) }
        } else {
            _uiState.update { it.copy(loading = true, scanning = true) }
        }
        viewModelScope.async(Dispatchers.IO) {
            getApplication<HomeApplication>().deviceRepository.byService("meteo")
                .forEach { device ->
                    viewModelScope.async(Dispatchers.IO) {
                        var available = true
                        try {
                            // TODO: Load the measurement for each available device
                            _measurement.value = MeteoAPI.retrofitService.getMeasurement(
                                "http://" + device.ip!!.hostAddress + "/")
                            // TODO: Load measurement history
                        } catch (err: Exception) {
                            available = false
                        }
                        if (device.available != available) {
                            device.available = available
                            getApplication<HomeApplication>().deviceRepository
                                .updateAvailability(device)
                        }
                    }
            }
            // TODO: Wait until selected device
            _uiState.update { it.copy(loading = false, scanning = false) }
        }
    }
}