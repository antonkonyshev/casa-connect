package com.github.antonkonyshev.casaconnect.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import com.github.antonkonyshev.casaconnect.data.network.DeviceModelSchema
import com.github.antonkonyshev.casaconnect.data.network.DevicePreferenceSchema
import com.github.antonkonyshev.casaconnect.data.network.MeteoServiceSchema
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Singleton
    @Provides
    fun provideMeteoServiceSchema(moshi: Moshi): MeteoServiceSchema {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost")
            .build().create(MeteoServiceSchema::class.java)
    }

    @Singleton
    @Provides
    fun provideDevicePreferenceSchema(moshi: Moshi): DevicePreferenceSchema {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost")
            .build().create(DevicePreferenceSchema::class.java)
    }

    @Singleton
    @Provides
    fun provideDeviceModelSchema(moshi: Moshi): DeviceModelSchema {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost")
            .build().create(DeviceModelSchema::class.java)
    }
}