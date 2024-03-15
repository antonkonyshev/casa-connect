package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.domain.repository.DevicePreferenceApiClient
import javax.inject.Inject

class SetDevicePreferencesUseCase @Inject constructor(
    private val client: DevicePreferenceApiClient
) {
    suspend operator fun invoke(preference: DevicePreference): Boolean {
        return client.setPreferences(preference)
    }
}