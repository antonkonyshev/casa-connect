package name.antonkonyshev.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.devices.discoverLocalDevices
import name.antonkonyshev.home.devices.getDeviceInfo
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.MeteoApi
import name.antonkonyshev.home.meteo.MeteoSensor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address
import java.util.Collections.addAll
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _scanning = MutableStateFlow(0)
    val scanning: StateFlow<Int> = _scanning

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _selectedDestination = MutableStateFlow(HomeDestinations.DEVICES)
    val selectedDestination: StateFlow<String> = _selectedDestination

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
        viewModelScope.launch {
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find{ la -> la.address is Inet4Address }
            if (linkAddress is LinkAddress) {
                val ipRange = SubnetUtils(linkAddress.toString()).info.allAddresses.filter filter@{
                    it != linkAddress.address.hostAddress
                }
                _scanning.value = ipRange.size
                _devices.value.forEach {
                    it.available = false
                }
                ipRange.forEach { ipAddress ->
                    val ip = Inet4Address.getByName(ipAddress)
                    if (ip.isMulticastAddress()) {
                        _scanning.update { it - 1 }
                        return@forEach
                    }
                    thread(true, true) {
                        if (ip.isReachable(500)) {
                            OkHttpClient().newCall(
                                Request.Builder().url("http://" + ip.hostAddress + "/service")
                                    .build()
                            ).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    _scanning.update { it - 1 }
                                }
                                override fun onResponse(call: Call, response: Response) {
                                    if (response.code == 200) {
                                        val moshi = Moshi.Builder()
                                            .add(KotlinJsonAdapterFactory())
                                            .build()
                                        response.body!!.source()
                                        val device = moshi.adapter(Device::class.java)
                                            .fromJson(response.body!!.source())
                                        if (devices.value.find {
                                            it.id == device!!.id
                                        } == null) {
                                            _devices.update {
                                                val newValue = it.toMutableList()
                                                val ms: MeteoSensor = MeteoSensor.fromDevice(device!!, ip, true)
                                                newValue.add(ms as Device)
                                                return@update newValue.toList()
                                            }
                                        }
                                        _scanning.update { it - 1 }
                                    } else {
                                        _scanning.update { it - 1 }
                                    }
                                }
                            })
                        } else {
                            _scanning.update { it - 1 }
                        }
                    }
                }
            }
            _loading.value = false
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