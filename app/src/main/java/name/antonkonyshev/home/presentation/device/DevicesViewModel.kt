package name.antonkonyshev.home.presentation.device

import name.antonkonyshev.home.data.database.DeviceRepositoryImpl
import name.antonkonyshev.home.data.network.DiscoveryServiceImpl
import name.antonkonyshev.home.domain.usecase.DiscoverDevicesInLocalNetworkUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicesByServiceUseCase
import name.antonkonyshev.home.presentation.BaseViewModel

class DevicesViewModel : BaseViewModel() {
    private val deviceRepository = DeviceRepositoryImpl
    private val discoveryService = DiscoveryServiceImpl

    val getDevicesByServiceUseCase = GetDevicesByServiceUseCase(deviceRepository)
    val discoverDevicesInLocalNetworkUseCase = DiscoverDevicesInLocalNetworkUseCase(
        discoveryService)
}