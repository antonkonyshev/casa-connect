package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceApiClient
import javax.inject.Inject

class GetDevicePreferencesUseCase @Inject constructor(
    private val client: DevicePreferenceApiClient
) {
    suspend operator fun invoke(device: Device): DevicePreference? {
        return client.getPreferences(device)
    }
}