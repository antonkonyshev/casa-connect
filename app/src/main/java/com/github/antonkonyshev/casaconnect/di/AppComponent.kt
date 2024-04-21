package com.github.antonkonyshev.casaconnect.di

import dagger.Component
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.presentation.device.DevicesViewModel
import com.github.antonkonyshev.casaconnect.presentation.devicepreference.DevicePreferenceViewModel
import com.github.antonkonyshev.casaconnect.presentation.door.DoorViewModel
import com.github.antonkonyshev.casaconnect.presentation.meteo.MeteoViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class, ImplBindingModule::class])
interface AppComponent {
    fun inject(application: CasaConnectApplication)
    fun inject(viewModel: DevicesViewModel)
    fun inject(viewModel: MeteoViewModel)
    fun inject(viewModel: DevicePreferenceViewModel)
    fun inject(viewModel: DoorViewModel)
}