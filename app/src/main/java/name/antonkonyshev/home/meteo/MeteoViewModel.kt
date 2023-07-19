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
import java.util.Date
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class DeviceMeasurement(val deviceId: String) {
    val _measurement = MutableStateFlow(Measurement())
    val measurementFlow = _measurement.asStateFlow()
    var lastHistoryUpdate = 0L
    val _history = MutableStateFlow<List<Measurement>>(emptyList())
    val historyFlow = _history.asStateFlow()
}

class MeteoViewModel(application: Application) : BaseViewModel(application) {
    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 15L  // seconds
    val measurements: HashMap<String, DeviceMeasurement> = HashMap()

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
                    if (!measurements.containsKey(device.id)) {
                        measurements[device.id] = DeviceMeasurement(device.id)
                    }
                    if (device.ip == null) {
                        return@forEach
                    }
                    viewModelScope.async(Dispatchers.IO) {
                        var available = device.available
                        if (!device.available) {
                            if (device.ip!!.isReachable(1000)) {
                                device.available = true
                            }
                        }
                        if (device.available) {
                            try {
                                measurements[device.id]!!._measurement.value = MeteoAPI
                                    .retrofitService.getMeasurement(device.getMeasurementUrl())
                            } catch (err: Exception) {
                                device.available = false
                            }
                        }
                        if (device.available != available) {
                            getApplication<HomeApplication>().deviceRepository
                                .updateAvailability(device)
                        }
                        // TODO: Add settings for the history update period
                        if (device.available && Date().getTime() > measurements[device.id]!!.lastHistoryUpdate + 1200000L) {
                            try {
                                measurements[device.id]!!._history.value = MeteoAPI
                                    .retrofitService.getHistory(device.getHistoryUrl())
                            } catch (err: Exception) {}
                        }
                    }
            }
            // TODO: Wait until selected device
            _uiState.update { it.copy(loading = false, scanning = false) }
        }
    }
}