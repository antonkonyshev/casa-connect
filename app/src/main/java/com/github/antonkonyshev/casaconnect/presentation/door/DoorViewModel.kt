package com.github.antonkonyshev.casaconnect.presentation.door

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicesByServiceUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.LoadCameraFrameUseCase
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

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying = _isPlaying.asStateFlow()

    private val _frame = MutableStateFlow<Bitmap>(
        Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565)
    )
    val frame = _frame.asStateFlow()

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var updateDeviceAvailabilityUseCase: UpdateDeviceAvailabilityUseCase

    @Inject
    lateinit var loadCameraFrameUseCase: LoadCameraFrameUseCase

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
                    loadCameraFrame()
                    return@forEach
                }
            }
            onLoaded()
        }
    }

    // TODO: Add unit tests
    fun loadCameraFrame() {
        if (device.value is Device) {
            viewModelScope.launch(Dispatchers.IO) {
                val bitmap = loadCameraFrameUseCase(device.value!!)
                if (bitmap != null) {
                    bitmap.prepareToDraw()
                    _frame.value = bitmap
                }
                if (isPlaying.value)
                    loadCameraFrame()
            }
        }
    }

    fun togglePlaying() {
        _isPlaying.value = !_isPlaying.value
        if (isPlaying.value)
            loadCameraFrame()
    }
}