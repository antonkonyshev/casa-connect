package name.antonkonyshev.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.MeteoApi

class HomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState

    init {
        observeMeasurement()
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
            _uiState.value = HomeUIState(
                measurement = MeteoApi.retrofitService.getMeasurement(),
                history = MeteoApi.retrofitService.getHistory(),
                loading = false
            )
        }
    }
}

data class HomeUIState(
    val measurement: Measurement = Measurement(),
    val history: List<Measurement> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
)