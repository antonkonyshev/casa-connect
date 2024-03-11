package name.antonkonyshev.home.presentation.meteo

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.domain.repository.DiscoveryService
import name.antonkonyshev.home.domain.usecase.GetDevicesByServiceUseCase
import name.antonkonyshev.home.domain.usecase.GetMeasurementFromMeteoSensorUseCase
import name.antonkonyshev.home.domain.usecase.UpdateDeviceAvailabilityUseCase
import name.antonkonyshev.home.presentation.BaseViewModel
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
    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 15L  // seconds
    private val measurementTimer = Timer()

    val measurements: HashMap<String, DeviceMeasurement> = HashMap()

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var updateDeviceAvailabilityUseCase: UpdateDeviceAvailabilityUseCase

    @Inject
    lateinit var getMeasurementFromMeteoSensorUseCase: GetMeasurementFromMeteoSensorUseCase

    @Inject
    lateinit var discoveryService: DiscoveryService

    init {
        onLoading()
        HomeApplication.instance.component.inject(this)
        viewModelScope.launch(Dispatchers.IO) {
            discoveryService.discoverDevices()
            observeMeasurement()
        }
        measurementTimer.scheduleAtFixedRate(
            periodicalMeasurementUpdate, periodicalMeasurementUpdate * 1000L
        ) {
            observeMeasurement(true)
        }
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
        // TODO: Move to data layer
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
            // TODO: Wait until selected device
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
        // TODO: Add settings for the history update period
        if (device.available && Date().time > measurements[device.id]!!
                .lastHistoryUpdate + 1200000L
        ) {
            measurements[device.id]!!.lastHistoryUpdate = Date().time
            val history = getMeasurementFromMeteoSensorUseCase.getHistory(device)
            if (history is List<Measurement>) {
                measurements[device.id]!!._history.value = history
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        measurementTimer.cancel()
    }
}