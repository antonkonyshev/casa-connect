package com.github.antonkonyshev.casaconnect.presentation.door

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicesByServiceUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.LoadCameraPictureUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.UpdateDeviceAvailabilityUseCase
import com.github.antonkonyshev.casaconnect.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DoorViewModel : BaseViewModel() {
    private val _device = MutableStateFlow<Device?>(null)
    val device = _device.asStateFlow()

    private val _picture = MutableStateFlow<Bitmap?>(null)
    val picture = _picture.asStateFlow()

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var updateDeviceAvailabilityUseCase: UpdateDeviceAvailabilityUseCase

    @Inject
    lateinit var loadCameraPictureUseCase: LoadCameraPictureUseCase

    init {
        CasaConnectApplication.instance.component.inject(this)
        selectAvailableDoorDevice()
    }

    fun selectAvailableDoorDevice() {
        if (uiState.value.scanning)
            return
        onLoading(true)
        viewModelScope.launch(Dispatchers.IO) {
            getDevicesByServiceUseCase.getDoorDevicesList().forEach { door ->
                if (door.ip == null)
                    return@forEach
                val device = updateDeviceAvailabilityUseCase.checkDeviceAvailability(door)
                if (device?.available ?: false) {
                    _device.value = device
                    loadCameraPicture()
                    return@forEach
                }
            }
            onLoaded()
        }
    }

    fun loadCameraPicture() {
        if (device.value is Device) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val picture = loadCameraPictureUseCase(device.value!!)
                    picture?.prepareToDraw()
                    _picture.value = picture
                } catch(_: Exception) {}
            }
        }
    }
}