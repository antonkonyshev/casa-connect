package name.antonkonyshev.home.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.BaseViewModel
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.devices.Device
import java.net.InetAddress

class DevicePreferenceViewModel(application: Application) : BaseViewModel(application) {
    private var deviceSelected = false

    private val _preference = MutableStateFlow(DevicePreference())
    val preference = _preference.asStateFlow()

    fun selectDevice(deviceId: String) {
        if (!deviceSelected) {
            viewModelScope.async(Dispatchers.IO) {
                val existingDevice = getApplication<HomeApplication>().deviceRepository
                    .byId(deviceId)
                if (existingDevice != null && existingDevice.ip is InetAddress) {
                    try {
                        val pref = DevicePreferenceAPI.service.getPreferences(
                            existingDevice.getPreferenceUrl()
                        )
                        pref.device = existingDevice
                        _preference.update { pref }
                    } catch (err: Exception) {}
                }
            }
        }
    }
}