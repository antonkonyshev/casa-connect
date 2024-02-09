package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DeviceRepository

class UpdateDeviceAvailabilityUseCase(private val repository: DeviceRepository) {

    operator fun invoke(device: DeviceModel) {
        repository.updateAvailability(device)
    }

}