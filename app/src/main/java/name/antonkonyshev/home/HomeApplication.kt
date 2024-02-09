package name.antonkonyshev.home

import android.app.Application
import name.antonkonyshev.home.data.network.DiscoveryService

class HomeApplication : Application() {
    val discoveryService by lazy { DiscoveryService.instance(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        discoveryService.discoverDevices()
    }

    companion object {
        @Volatile
        lateinit var instance: HomeApplication
            private set
    }
}