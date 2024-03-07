package name.antonkonyshev.home.presentation.device

import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.usecase.DiscoverDevicesInLocalNetworkUseCase
import name.antonkonyshev.home.domain.usecase.GetDevicesByServiceUseCase
import name.antonkonyshev.home.presentation.BaseViewModel
import javax.inject.Inject

class DevicesViewModel : BaseViewModel() {

    @Inject
    lateinit var getDevicesByServiceUseCase: GetDevicesByServiceUseCase

    @Inject
    lateinit var discoverDevicesInLocalNetworkUseCase: DiscoverDevicesInLocalNetworkUseCase

    init {
        HomeApplication.instance.component.inject(this)
    }
}