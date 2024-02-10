package name.antonkonyshev.home.presentation.devicepreference

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.data.database.DeviceRepositoryImpl
import name.antonkonyshev.home.data.network.DevicePreferenceAPI
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.usecase.GetDeviceByIdUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicePreferencesUseCase
import name.antonkonyshev.home.presentation.BaseViewModel
import java.net.InetAddress

class DevicePreferenceViewModel : BaseViewModel() {
    private var deviceSelected = false

    private val _preference = MutableStateFlow(DevicePreference())
    val preference = _preference.asStateFlow()

    private val deviceRepository = DeviceRepositoryImpl
    private val devicePreferenceService = DevicePreferenceAPI

    val getDeviceByIdUseCase = GetDeviceByIdUseCase(deviceRepository)
    val getDevicePreferenceUseCase = GetDevicePreferencesUseCase(devicePreferenceService)

    fun selectDevice(deviceId: String) {
        if (!deviceSelected) {
            viewModelScope.async(Dispatchers.IO) {
                val existingDevice = getDeviceByIdUseCase(deviceId)
                if (existingDevice != null && existingDevice.ip is InetAddress) {
                    _preference.update { getDevicePreferenceUseCase(existingDevice) }
                }
            }
        }
    }
}