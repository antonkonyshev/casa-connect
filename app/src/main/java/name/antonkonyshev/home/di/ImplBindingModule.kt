package name.antonkonyshev.home.di

import dagger.Binds
import dagger.Module
import name.antonkonyshev.home.data.database.DeviceRepositoryImpl
import name.antonkonyshev.home.data.network.DevicePreferenceApiClientImpl
import name.antonkonyshev.home.data.network.DiscoveryServiceImpl
import name.antonkonyshev.home.data.network.MeteoApiClientImpl
import name.antonkonyshev.home.domain.repository.DevicePreferenceApiClient
import name.antonkonyshev.home.domain.repository.DeviceRepository
import name.antonkonyshev.home.domain.repository.DiscoveryService
import name.antonkonyshev.home.domain.repository.MeteoApiClient

@Module
interface ImplBindingModule {

    @Binds
    fun bindDeviceRepository(repository: DeviceRepositoryImpl): DeviceRepository

    @Binds
    fun bindDiscoverService(service: DiscoveryServiceImpl): DiscoveryService

    @Binds
    fun bindMeteoService(service: MeteoApiClientImpl): MeteoApiClient

    @Binds
    fun bindDevicePreferenceService(service: DevicePreferenceApiClientImpl): DevicePreferenceApiClient
}