package name.antonkonyshev.home

import android.app.Application
import name.antonkonyshev.home.data.network.DiscoveryServiceImpl

class HomeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        DiscoveryServiceImpl.discoverDevices()
    }

    companion object {
        @Volatile
        lateinit var instance: HomeApplication
            private set
    }
}