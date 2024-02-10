package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.repository.DiscoveryService

class DiscoverDevicesInLocalNetworkUseCase(val service: DiscoveryService) {
    operator fun invoke() {
        service.discoverDevices()
    }
}