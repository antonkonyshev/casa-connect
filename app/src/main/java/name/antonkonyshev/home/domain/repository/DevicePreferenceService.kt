package name.antonkonyshev.home.domain.repository

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference

interface DevicePreferenceService {
    suspend fun getPreferences(device: Device): DevicePreference
    suspend fun setPreferences(preference: DevicePreference): Boolean
}