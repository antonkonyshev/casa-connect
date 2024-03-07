package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.repository.DiscoveryService
import javax.inject.Inject

class DiscoverDevicesInLocalNetworkUseCase @Inject constructor(
    private val service: DiscoveryService
) {
    operator fun invoke() {
        service.discoverDevices()
    }
}