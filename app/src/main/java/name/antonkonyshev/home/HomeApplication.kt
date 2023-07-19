package name.antonkonyshev.home

import android.app.Application
import name.antonkonyshev.home.devices.DeviceRepository
import name.antonkonyshev.home.devices.DiscoveryService

class HomeApplication : Application() {
    val database by lazy { AppDatabase.instance(this) }
    val deviceRepository by lazy { DeviceRepository(database.deviceDao()) }
    val discoveryService by lazy { DiscoveryService.instance(this) }

    override fun onCreate() {
        super.onCreate()
        discoveryService.discoverDevices()
    }
}