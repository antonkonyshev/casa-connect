package name.antonkonyshev.home.di

import dagger.Component
import name.antonkonyshev.home.HomeApplication
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.presentation.device.DevicesViewModel
import name.antonkonyshev.home.presentation.devicepreference.DevicePreferenceViewModel
import name.antonkonyshev.home.presentation.meteo.MeteoViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class, ImplBindingModule::class])
interface AppComponent {
    // TODO: Check singletons
    fun inject(application: HomeApplication)
    fun inject(viewModel: DevicesViewModel)
    fun inject(viewModel: MeteoViewModel)
    fun inject(viewModel: DevicePreferenceViewModel)
}