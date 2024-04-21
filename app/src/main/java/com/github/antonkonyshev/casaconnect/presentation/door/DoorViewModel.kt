package com.github.antonkonyshev.casaconnect.presentation.door

import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.domain.usecase.GetDevicesByServiceUseCase
import com.github.antonkonyshev.casaconnect.domain.usecase.UpdateDeviceAvailabilityUseCase
import com.github.antonkonyshev.casaconnect.presentation.common.BaseViewModel
import java.util.Timer
import javax.inject.Inject

class DoorViewModel : BaseViewModel() {
    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var updateDeviceAvailabilityUseCase: UpdateDeviceAvailabilityUseCase

    init {
        CasaConnectApplication.instance.component.inject(this)
    }
}