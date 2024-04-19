package com.github.antonkonyshev.casaconnect.presentation.meteo

import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.Measurement
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicesByServiceUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.GetMeasurementFromMeteoSensorUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.UpdateDeviceAvailabilityUseCase
import com.github.antonkonyshev.casaconnect.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.scheduleAtFixedRate

data class DeviceMeasurement(val deviceId: String) {
    val _measurement: MutableStateFlow<Measurement> = MutableStateFlow(Measurement())
    val measurementFlow: StateFlow<Measurement> = _measurement.asStateFlow()
    var lastHistoryUpdate: Long = 0L
    val _history: MutableStateFlow<List<Measurement>> = MutableStateFlow(emptyList())
    val historyFlow: StateFlow<List<Measurement>> = _history.asStateFlow()
}

class MeteoViewModel : BaseViewModel() {

    val measurements: HashMap<String, DeviceMeasurement> = HashMap()
    var measurementHistoryUpdatePeriod: Long = 600000L

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var updateDeviceAvailabilityUseCase: UpdateDeviceAvailabilityUseCase

    @Inject
    lateinit var getMeasurementFromMeteoSensorUseCase: GetMeasurementFromMeteoSensorUseCase

    private var measurementTimer: Timer? = null

    init {
        CasaConnectApplication.instance.component.inject(this)
        observeMeasurement()
        startMeasurementTimer()
    }

    fun observeMeasurement(silent: Boolean = false) {
        if (uiState.value.scanning) {
            return
        }
        if (silent) {
            _uiState.update { it.copy(scanning = true) }
        } else {
            _uiState.update { it.copy(loading = true, scanning = true) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getDevicesByServiceUseCase.getMeteoDevicesList().forEach { device ->
                if (!measurements.containsKey(device.id)) {
                    measurements[device.id] = DeviceMeasurement(device.id)
                }
                if (device.ip == null) {
                    return@forEach
                }
                viewModelScope.launch(Dispatchers.IO) {
                    retrieveDeviceMeasurement(device)
                }
            }
            _uiState.update { it.copy(loading = false, scanning = false) }
        }
    }

    private suspend fun retrieveDeviceMeasurement(device: Device) {
        val available = device.available
        val measurement = getMeasurementFromMeteoSensorUseCase.getMeasurement(device)
        if (measurement is Measurement) {
            measurements[device.id]!!._measurement.value = measurement
        }
        if (device.available != available) {
            updateDeviceAvailabilityUseCase(device)
        }
        retrieveDeviceHistory(device)
    }

    private suspend fun retrieveDeviceHistory(device: Device) {
        if (device.available && Date().time > measurements[device.id]!!
                .lastHistoryUpdate + measurementHistoryUpdatePeriod
        ) {
            measurements[device.id]!!.lastHistoryUpdate = Date().time
            val history = getMeasurementFromMeteoSensorUseCase.getHistory(device)
            if (history is List<Measurement>) {
                measurements[device.id]!!._history.value = history
            }
        }
    }

    fun startMeasurementTimer() {
        val settings =
            PreferenceManager.getDefaultSharedPreferences(CasaConnectApplication.instance)
        val measurementUpdatePeriod: Long = 1000L * try {
            settings.getString("measurement_update_period", "15")!!.toLong()
        } catch (_: Exception) {
            15L
        }

        try {
            measurementHistoryUpdatePeriod = settings.getString(
                "measurement_history_update_period", "600"
            )!!.toLong() * 1000L
        } catch (_: Exception) {
        }

        if (measurementTimer == null) {
            measurementTimer = Timer()
        }
        measurementTimer?.scheduleAtFixedRate(measurementUpdatePeriod, measurementUpdatePeriod) {
            observeMeasurement(true)
        }
    }

    fun stopMeasurementTimer() {
        measurementTimer?.cancel()
        measurementTimer = null
    }

    override fun onCleared() {
        stopMeasurementTimer()
        super.onCleared()
    }
}