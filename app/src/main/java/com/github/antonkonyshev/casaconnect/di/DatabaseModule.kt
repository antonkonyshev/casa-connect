package com.github.antonkonyshev.casaconnect.di

import dagger.Module
import dagger.Provides
import com.github.antonkonyshev.casaconnect.CasaConnectApplication
import com.github.antonkonyshev.casaconnect.data.database.AppDatabase

@Module
class DatabaseModule {

    @Provides
    fun provideAppDatabase(): AppDatabase {
        return AppDatabase.instance(CasaConnectApplication.instance)
    }
}