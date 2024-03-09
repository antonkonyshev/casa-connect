package name.antonkonyshev.home.presentation.devicepreference

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.usecase.GetDeviceByIdUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicePreferencesUseCase
import name.antonkonyshev.home.domain.usecase.SetDevicePreferencesUseCase
import name.antonkonyshev.home.presentation.BaseViewModel
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
}