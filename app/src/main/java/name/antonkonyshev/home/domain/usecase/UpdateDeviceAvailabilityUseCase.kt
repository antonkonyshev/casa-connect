package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.repository.DeviceRepository
import javax.inject.Inject

class UpdateDeviceAvailabilityUseCase @Inject constructor(
    private val repository: DeviceRepository
) {

    operator fun invoke(device: Device) {
        repository.updateAvailability(device)
    }

}