package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.domain.repository.DevicePreferenceApiClient
import javax.inject.Inject

class GetDevicePreferencesUseCase @Inject constructor(
    private val client: DevicePreferenceApiClient
) {
    suspend operator fun invoke(device: Device): DevicePreference? {
        return client.getPreferences(device)
    }
}