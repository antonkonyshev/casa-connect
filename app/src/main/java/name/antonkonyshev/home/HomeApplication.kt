package name.antonkonyshev.home

import android.app.Application
import name.antonkonyshev.home.di.DaggerAppComponent

class HomeApplication : Application() {

    val component by lazy { DaggerAppComponent.create() }

    override fun onCreate() {
        instance = this
        component.inject(this)
        super.onCreate()
    }

    companion object {
        @Volatile
        lateinit var instance: HomeApplication
            private set
    }
}