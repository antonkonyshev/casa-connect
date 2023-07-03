package name.antonkonyshev.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.MeteoApi
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState

    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 60L  // seconds

    private var backgroundResources: ArrayList<Int> = arrayListOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    )
    val meteoBackgroundResource: Int = backgroundResources.shuffled().drop(0)[0]
    val navigationDrawerBackgroundResource: Int = backgroundResources.shuffled().drop(0)[0]

    init {
        // observeMeasurement()
        Timer().scheduleAtFixedRate(0L, periodicalMeasurementUpdate * 1000L) {
            observeMeasurement()
        }
    }

    fun observeMeasurement() {
        if (_uiState.value.loading) {
            return
        }
        _uiState.value = HomeUIState(
            measurement = _uiState.value.measurement,
            history = _uiState.value.history,
            loading = true
        )
        viewModelScope.launch {
            var error: String? = null
            try {
                _uiState.value = HomeUIState(
                    measurement = MeteoApi.retrofitService.getMeasurement(),
                    history = _uiState.value.history,
                )
                try {
                    _uiState.value = HomeUIState(
                        measurement = _uiState.value.measurement,
                        history = MeteoApi.retrofitService.getHistory(),
                    )
                } catch (err: Exception) {
                    error = "Error occured on connecting to the meteo sensors"
                }
            } catch (err: Exception) {
                error = "Error occured on connecting to the meteo sensors"
            }
            if (error != null) {
                _uiState.value = HomeUIState(
                    measurement = _uiState.value.measurement,
                    history = _uiState.value.history,
                    error = error,
                )
            }
        }
    }
}

data class HomeUIState(
    val measurement: Measurement = Measurement(),
    val history: List<Measurement> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)