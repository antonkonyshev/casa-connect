package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.repository.DeviceRepository
import javax.inject.Inject

class GetDeviceByIdUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(id: String): Device? {
        return repository.byId(id)
    }
}