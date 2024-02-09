package name.antonkonyshev.home.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.settings.DevicePreference
import name.antonkonyshev.home.data.network.DevicePreferenceAPI
import name.antonkonyshev.home.data.DeviceRepositoryImpl
import name.antonkonyshev.home.domain.usecase.GetDeviceByIdUseCase
import java.net.InetAddress

class DevicePreferenceViewModel() : BaseViewModel() {
    private var deviceSelected = false

    private val _preference = MutableStateFlow(DevicePreference())
    val preference = _preference.asStateFlow()

    private val deviceRepository = DeviceRepositoryImpl

    val getDeviceByIdUseCase = GetDeviceByIdUseCase(deviceRepository)

    fun selectDevice(deviceId: String) {
        if (!deviceSelected) {
            viewModelScope.async(Dispatchers.IO) {
                val existingDevice = getDeviceByIdUseCase(deviceId)
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