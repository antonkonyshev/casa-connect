package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceApiClient
import javax.inject.Inject

class SetDevicePreferencesUseCase @Inject constructor(
    private val service: DevicePreferenceApiClient
) {
    suspend operator fun invoke(preference: DevicePreference): Boolean {
        return service.setPreferences(preference)
    }
}