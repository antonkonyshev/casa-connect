package name.antonkonyshev.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.devices.DiscoveryService
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.MeteoApi
import java.net.Inet4Address
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _scanning = MutableStateFlow(0)
    val scanning: StateFlow<Int> = _scanning

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _selectedDestination = MutableStateFlow(NavigationDestinations.DEVICES)
    val selectedDestination: StateFlow<String> = _selectedDestination

    // TODO: Add settings for the period of the measurement updates
    private val periodicalMeasurementUpdate = 60L  // seconds

    private var backgroundResources: ArrayList<Int> = arrayListOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    )
    val backgroundResource: Int = backgroundResources.shuffled().drop(0)[0]
    val navigationBackgroundResource: Int = backgroundResources.shuffled().drop(0)[0]

    init {
        Timer().scheduleAtFixedRate(0L, periodicalMeasurementUpdate * 1000L) {
            observeMeasurement()
        }
        discoverDevices()
    }

    fun selectDestination(destination: String) {
        _selectedDestination.value = destination
    }

    fun discoverDevices() {
        if (loading.value) {
            return;
        }
        _loading.value = true
        viewModelScope.async(Dispatchers.IO) {
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find{ la -> la.address is Inet4Address }
            if (linkAddress is LinkAddress) {
                DiscoveryService.discoverLocalDevices(linkAddress, _devices)
            }
            _loading.value = false
            Log.i("home", "Loading complete " + devices.value.size)
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
                    error = "Error occurred on connecting to the meteo sensors"
                }
            } catch (err: Exception) {
                error = "Error occurred on connecting to the meteo sensors"
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