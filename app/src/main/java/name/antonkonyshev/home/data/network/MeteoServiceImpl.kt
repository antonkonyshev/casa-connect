package name.antonkonyshev.home.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.domain.repository.MeteoService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface MeteoServiceSchema {
    @GET
    suspend fun getMeasurement(@Url url: String): Measurement

    @GET
    suspend fun getHistory(@Url url: String): List<Measurement>
}

object MeteoAPI : MeteoService {
    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val service: MeteoServiceSchema by lazy {
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost").build().create(MeteoServiceSchema::class.java)
    }

    override suspend fun getMeasurement(device: Device): Measurement? {
        var measurement: Measurement? = null
        if (!device.available) {
            if (device.ip!!.isReachable(1000)) {
                device.available = true
            }
        }
        if (device.available) {
            try {
                measurement = service.getMeasurement(
                    NetworkDevice.fromDevice(device).getMeasurementUrl()
                )
            } catch (err: Exception) {
                device.available = false
            }
        }
        return measurement
    }

    override suspend fun getHistory(device: Device): List<Measurement>? {
        try {
            return service.getHistory(
                NetworkDevice.fromDevice(device).getHistoryUrl()
            )
        } catch (_: Exception) {}
        return null
    }
}