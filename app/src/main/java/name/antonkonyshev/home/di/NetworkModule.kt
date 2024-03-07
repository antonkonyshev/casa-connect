package name.antonkonyshev.home.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import name.antonkonyshev.home.data.network.DevicePreferenceSchema
import name.antonkonyshev.home.data.network.MeteoServiceSchema
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
            .baseUrl("http://localhost").build()
            .create(MeteoServiceSchema::class.java)
    }

    @Singleton
    @Provides
    fun provideDevicePreferenceSchema(moshi: Moshi): DevicePreferenceSchema {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost").build()
            .create(DevicePreferenceSchema::class.java)
    }
}