package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.HomeApplication

class DiscoverDevicesInLocalNetworkUseCase {
    operator fun invoke() {
        HomeApplication.instance.discoveryService.discoverDevices()
    }
}