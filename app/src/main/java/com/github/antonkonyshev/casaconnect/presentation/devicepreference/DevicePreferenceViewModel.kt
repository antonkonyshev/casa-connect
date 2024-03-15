package com.github.antonkonyshev.casaconnect.presentation.devicepreference

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicePreferencesUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.SetDevicePreferencesUseCase
import com.github.antonkonyshev.casaconnect.presentation.BaseViewModel
import java.net.InetAddress
import javax.inject.Inject

class DevicePreferenceViewModel : BaseViewModel() {
    var selectedDevice: Device? = null

    @Inject
    lateinit var getDevicePreferenceUseCase: GetDevicePreferencesUseCase

    @Inject
    lateinit var setDevicePreferenceUseCase: SetDevicePreferencesUseCase

    var deviceName by mutableStateOf("")
    var highPollution by mutableIntStateOf(20)
    var minTemperature by mutableIntStateOf(14)
    var maxTemperature by mutableIntStateOf(24)
    var measurementPeriod by mutableIntStateOf(15)
    var timeSyncPeriod by mutableIntStateOf(1800)
    var historyLength by mutableIntStateOf(50)
    var historyRecordPeriod by mutableIntStateOf(1800)
    var wifiSsid by mutableStateOf("")

    init {
        CasaConnectApplication.instance.component.inject(this)
    }

    fun prepareData(callback: (Boolean) -> Unit) {
        if (selectedDevice == null) {
            callback(false)
            return
        }
        onLoading()
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedDevice?.ip is InetAddress) {
                val preference = getDevicePreferenceUseCase(selectedDevice!!)
                if (preference != null) {
                    deviceName = selectedDevice?.name ?: ""
                    highPollution = preference.highPollution
                    minTemperature = preference.minTemperature
                    maxTemperature = preference.maxTemperature
                    measurementPeriod = preference.measurementPeriod
                    timeSyncPeriod = preference.timeSyncPeriod
                    historyLength = preference.historyLength
                    historyRecordPeriod = preference.historyRecordPeriod
                    wifiSsid = preference.wifiSsid
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
        if (selectedDevice != null) {
            onLoading()
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    callback(
                        setDevicePreferenceUseCase(
                            DevicePreference(
                                highPollution, minTemperature, maxTemperature,
                                measurementPeriod, timeSyncPeriod,
                                historyLength, historyRecordPeriod, "",
                                selectedDevice
                            )
                        )
                    )
                } catch (_: Exception) {}
                onLoaded()
                return@launch
            }
        } else {
            callback(false)
        }
    }
}