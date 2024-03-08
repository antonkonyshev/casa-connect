package name.antonkonyshev.home.presentation.devicepreference

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.data.network.DevicePreferenceAPI
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DeviceRepository
import name.antonkonyshev.home.domain.usecase.GetDeviceByIdUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicePreferencesUseCase
import name.antonkonyshev.home.domain.usecase.SetDevicePreferencesUseCase
import name.antonkonyshev.home.domain.usecase.SetDevicePreferencesUseCase_Factory
import name.antonkonyshev.home.presentation.BaseViewModel
import name.antonkonyshev.home.presentation.device.DevicesActivity
import java.net.InetAddress
import javax.inject.Inject

class DevicePreferenceViewModel : BaseViewModel() {
    var selectedDevice: Device? = null

    @Inject
    lateinit var getDeviceByIdUseCase: GetDeviceByIdUseCase

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
        HomeApplication.instance.component.inject(this)
    }

    fun selectDevice(deviceId: String) {
        if (selectedDevice == null) {
            viewModelScope.async(Dispatchers.IO) {
                selectedDevice = getDeviceByIdUseCase(deviceId)
                if (selectedDevice != null && selectedDevice?.ip is InetAddress) {
                    val preference = getDevicePreferenceUseCase(selectedDevice!!)
                    deviceName = selectedDevice?.name ?: ""
                    highPollution = preference.highPollution
                    minTemperature = preference.minTemperature
                    maxTemperature = preference.maxTemperature
                    measurementPeriod = preference.measurementPeriod
                    timeSyncPeriod = preference.timeSyncPeriod
                    historyLength = preference.historyLength
                    historyRecordPeriod = preference.historyRecordPeriod
                    wifiSsid = preference.wifiSsid
                }
            }
        }
    }

    fun saveDevicePreference(context: Context) {
        if (selectedDevice != null) {
            viewModelScope.async(Dispatchers.IO) {
                val result = setDevicePreferenceUseCase(
                    DevicePreference(
                        highPollution, minTemperature, maxTemperature, measurementPeriod,
                        timeSyncPeriod, historyLength, historyRecordPeriod, "",
                        selectedDevice
                    )
                )
                if (result) {
                    startActivity(
                        context, Intent(context, DevicesActivity::class.java), null
                    )
                }
            }
        }
    }
}