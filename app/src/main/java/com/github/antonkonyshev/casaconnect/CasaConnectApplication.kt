package com.github.antonkonyshev.casaconnect

import android.app.Application
import com.github.antonkonyshev.casaconnect.di.AppComponent
import com.github.antonkonyshev.casaconnect.di.DaggerAppComponent

class CasaConnectApplication : Application() {

    val component by lazy<AppComponent> { DaggerAppComponent.create() }

    override fun onCreate() {
        instance = this
        component.inject(this)
        super.onCreate()
    }

    companion object {
        @Volatile
        lateinit var instance: CasaConnectApplication
            private set
    }
}