package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.data.database.DeviceModel
import name.antonkonyshev.home.domain.repository.DeviceRepository

class GetDeviceByIdUseCase(private val repository: DeviceRepository) {
    operator fun invoke(id: String): DeviceModel? {
        return repository.byId(id)
    }
}