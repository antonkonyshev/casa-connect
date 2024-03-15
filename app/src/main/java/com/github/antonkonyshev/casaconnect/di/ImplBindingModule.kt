package com.github.antonkonyshev.casaconnect.di

import dagger.Binds
import dagger.Module
import com.github.antonkonyshev.casaconnect.data.database.DeviceRepositoryImpl
import com.github.antonkonyshev.casaconnect.data.network.DevicePreferenceApiClientImpl
import com.github.antonkonyshev.casaconnect.data.network.DiscoveryServiceImpl
import com.github.antonkonyshev.casaconnect.data.network.MeteoApiClientImpl
import com.github.antonkonyshev.casaconnect.domain.repository.DevicePreferenceApiClient
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import com.github.antonkonyshev.casaconnect.domain.repository.DiscoveryService
import com.github.antonkonyshev.casaconnect.domain.repository.MeteoApiClient

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