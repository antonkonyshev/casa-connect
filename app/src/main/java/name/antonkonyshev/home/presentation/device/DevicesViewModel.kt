package name.antonkonyshev.home.presentation.device

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.usecase.DiscoverDevicesInLocalNetworkUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicesByServiceUseCase
import name.antonkonyshev.home.presentation.BaseViewModel
import javax.inject.Inject

class DevicesViewModel : BaseViewModel() {

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var discoverDevicesInLocalNetworkUseCase: DiscoverDevicesInLocalNetworkUseCase

    init {
        HomeApplication.instance.component.inject(this)
    }

    var _selectedDevice = MutableStateFlow<Device?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    fun discoverDevices() {
        onLoading()
        viewModelScope.launch(Dispatchers.IO) {
            discoverDevicesInLocalNetworkUseCase()
            onLoaded()
        }
    }
}