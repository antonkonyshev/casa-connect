package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceApiClient
import javax.inject.Inject

class SetDevicePreferencesUseCase @Inject constructor(
    private val client: DevicePreferenceApiClient
) {
    suspend operator fun invoke(preference: DevicePreference): Boolean {
        return client.setPreferences(preference)
    }
}