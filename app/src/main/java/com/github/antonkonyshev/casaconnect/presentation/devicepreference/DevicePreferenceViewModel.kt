package com.github.antonkonyshev.casaconnect.presentation.devicepreference

import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicePreferencesUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.SetDeviceNameUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.SetDevicePreferencesUseCase
import com.github.antonkonyshev.casaconnect.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

class DevicePreferenceViewModel : BaseViewModel() {
    var selectedDevice: Device? = null

    private val _preference = MutableStateFlow<DevicePreference?>(null)
    val preference = _preference.asStateFlow()

    @Inject
    lateinit var getDevicePreferenceUseCase: GetDevicePreferencesUseCase

    @Inject
    lateinit var setDevicePreferenceUseCase: SetDevicePreferencesUseCase

    @Inject
    lateinit var setDeviceNameUseCase: SetDeviceNameUseCase

    init {
        CasaConnectApplication.instance.component.inject(this)
    }

    fun selectDevice(device: Device) {
        if (selectedDevice == null) {
            selectedDevice = device
            prepareData { if (!it) deselectDevice() }
        }
    }

    fun deselectDevice() {
        selectedDevice = null
    }

    fun prepareData(callback: (Boolean) -> Unit) {
        if (selectedDevice == null || uiState.value.loading) {
            callback(false)
            return
        }
        onLoading()
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedDevice?.ip is InetAddress) {
                _preference.value = getDevicePreferenceUseCase(selectedDevice!!)
                if (preference.value != null) {
                    onLoaded()
                    callback(true)
                    return@launch
                }
            }
            onLoaded()
            callback(false)
        }
    }

    fun saveDevicePreference(callback: (Boolean) -> Unit) {
        if (selectedDevice != null && !uiState.value.loading) {
            onLoading()
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    callback(setDevicePreferenceUseCase(preference.value!!))
                    preference.value!!.device?.let { setDeviceNameUseCase(it) }
                } catch (_: Exception) {
                    callback(false)
                }
                onLoaded()
                return@launch
            }
        } else {
            callback(false)
        }
    }
}