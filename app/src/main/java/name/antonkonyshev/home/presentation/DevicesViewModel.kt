package name.antonkonyshev.home.presentation

import name.antonkonyshev.home.data.DeviceRepositoryImpl
import name.antonkonyshev.home.domain.usecase.DiscoverDevicesInLocalNetworkUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicesByServiceUseCase

class DevicesViewModel() : BaseViewModel() {
    private val deviceRepository = DeviceRepositoryImpl

    val getDevicesByServiceUseCase = GetDevicesByServiceUseCase(deviceRepository)
    val discoverDevicesInLocalNetworkUseCase = DiscoverDevicesInLocalNetworkUseCase()
}