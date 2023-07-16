package name.antonkonyshev.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.devices.DiscoveryService
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.apache.commons.net.util.SubnetUtils
import java.io.IOException
import java.net.Inet4Address
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val devicesStateUpdatePeriod = 60L  // seconds

    protected val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    protected val _devices = MutableStateFlow(emptyList<Device>())
    val devices = _devices.asStateFlow()

    protected val backgroundResources = listOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    ).shuffled()
    val backgroundResource = backgroundResources[0]
    val navigationBackgroundResource = backgroundResources.drop(1)[0]

    init {
        Timer().scheduleAtFixedRate(delay = 0L, period = devicesStateUpdatePeriod * 1000L) {
            discoverDevices()
        }
    }

    fun discoverDevices() {
        if (uiState.value.scanning) { return }
        _uiState.update { it.copy(scanning = true) }

        viewModelScope.async(Dispatchers.IO) {
            val connectivityManager = getApplication<Application>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val linkAddress: LinkAddress? = connectivityManager.getLinkProperties(
                connectivityManager.activeNetwork
            )?.linkAddresses?.find { la -> la.address is Inet4Address }
            if (linkAddress is LinkAddress) {
                DiscoveryService.discoverLocalDevices(linkAddress, _devices)
            }
            _uiState.update { it.copy(scanning = false) }
        }
    }
}

data class UiState(
    val loading: Boolean = false,
    val scanning: Boolean = false,
)
