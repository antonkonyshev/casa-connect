package com.github.antonkonyshev.casaconnect.di

import com.github.antonkonyshev.casaconnect.data.database.DeviceRepositoryImpl
import com.github.antonkonyshev.casaconnect.data.network.CameraApiClientImpl
import com.github.antonkonyshev.casaconnect.data.network.DevicePreferenceApiClientImpl
import com.github.antonkonyshev.casaconnect.data.network.DiscoveryServiceImpl
import com.github.antonkonyshev.casaconnect.data.network.MeteoApiClientImpl
import com.github.antonkonyshev.casaconnect.domain.repository.CameraApiClient
import com.github.antonkonyshev.casaconnect.domain.repository.DevicePreferenceApiClient
import com.github.antonkonyshev.casaconnect.domain.repository.DeviceRepository
import com.github.antonkonyshev.casaconnect.domain.repository.DiscoveryService
import com.github.antonkonyshev.casaconnect.domain.repository.MeteoApiClient
import dagger.Binds
import dagger.Module

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

    @Binds
    fun bindCameraService(service: CameraApiClientImpl): CameraApiClient
}