package com.github.antonkonyshev.casaconnect.presentation.device

import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.usecase.DiscoverDevicesInLocalNetworkUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicesByServiceUseCase
import com.github.antonkonyshev.casaconnect.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DevicesViewModel : BaseViewModel() {

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var discoverDevicesInLocalNetworkUseCase: DiscoverDevicesInLocalNetworkUseCase

    init {
        CasaConnectApplication.instance.component.inject(this)
    }

    private var _selectedDevice = MutableStateFlow<Device?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    fun discoverDevices() {
        onLoading()
        viewModelScope.launch(Dispatchers.IO) {
            discoverDevicesInLocalNetworkUseCase()
            onLoaded()
        }
    }

    fun selectDevice(device: Device) {
        _selectedDevice.value = device
    }

    fun deselectDevice() {
        _selectedDevice.value = null
    }
}