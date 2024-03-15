package com.github.antonkonyshev.casaconnect.domain.repository

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference

interface DevicePreferenceApiClient {
    suspend fun getPreferences(device: Device): DevicePreference?
    suspend fun setPreferences(preference: DevicePreference): Boolean
}