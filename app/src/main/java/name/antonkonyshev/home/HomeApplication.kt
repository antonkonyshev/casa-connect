package name.antonkonyshev.home

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import name.antonkonyshev.home.di.DaggerAppComponent
import name.antonkonyshev.home.domain.repository.DiscoveryService
import javax.inject.Inject

class HomeApplication : Application() {
    @Inject
    lateinit var discoveryService: DiscoveryService

    val component by lazy { DaggerAppComponent.create() }

    override fun onCreate() {
        instance = this
        component.inject(this)
        super.onCreate()
        CoroutineScope(Dispatchers.IO).async {
            discoveryService.discoverDevices()
        }
    }

    companion object {
        @Volatile
        lateinit var instance: HomeApplication
            private set
    }
}