package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceService

class GetDevicePreferencesUseCase(val service: DevicePreferenceService) {
    suspend operator fun invoke(device: Device): DevicePreference {
        return service.getPreferences(device)
    }
}