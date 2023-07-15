package name.antonkonyshev.home.devices

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import name.antonkonyshev.home.BaseViewModel
import java.net.Inet4Address
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class DevicesViewModel(application: Application) : BaseViewModel(application) {
    private val devicesStateUpdatePeriod = 60L  // seconds

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    val backgroundResources = AvailableBackgroundResources.toList().shuffled()
    val backgroundResource = backgroundResources.get(0)
    val navigationBackgroundResource = backgroundResources.get(1)

    init {
        discoverDevices()
        Timer().scheduleAtFixedRate(
            delay = devicesStateUpdatePeriod, period = devicesStateUpdatePeriod * 1000L
        ) { updateDevicesState() }
    }

    fun updateDevicesState() {
        // TODO: implement
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
        }
    }
}