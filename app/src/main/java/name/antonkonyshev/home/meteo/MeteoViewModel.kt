package name.antonkonyshev.home.meteo

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.BaseViewModel
import name.antonkonyshev.home.devices.DiscoveryService
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MeteoViewModel(application: Application) : BaseViewModel(application) {
    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 60L  // seconds

    private val _measurement = MutableStateFlow(Measurement())
    val measurement = _measurement.asStateFlow()

    private val _history = MutableStateFlow(emptyList<Measurement>())
    val history = _history.asStateFlow()

    init {
        Timer().scheduleAtFixedRate(0L, periodicalMeasurementUpdate * 1000L) {
            observeMeasurement()
        }
    }

    fun observeMeasurement() {
        if (uiState.value.loading) { return }
        _uiState.update { it.copy(loading = true) }
        viewModelScope.async(Dispatchers.IO) {
            try {
                _measurement.value = MeteoApi.retrofitService.getMeasurement()
                try {
                    _history.value = MeteoApi.retrofitService.getHistory()
                } catch (err: Exception) {}
            } catch (err: Exception) {}
            _uiState.update { it.copy(loading = false) }
        }
    }
}