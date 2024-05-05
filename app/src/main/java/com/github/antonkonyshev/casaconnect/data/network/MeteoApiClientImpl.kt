package com.github.antonkonyshev.casaconnect.data.network

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.Measurement
import com.github.antonkonyshev.casaconnect.domain.repository.MeteoApiClient
import retrofit2.http.GET
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface MeteoServiceSchema {
    @GET
    suspend fun getMeasurement(@Url url: String): Measurement

    @GET
    suspend fun getHistory(@Url url: String): List<Measurement>
}

@Singleton
class MeteoApiClientImpl @Inject constructor(
    private val service: MeteoServiceSchema
) : MeteoApiClient {

    override suspend fun getMeasurement(device: Device): Measurement? {
        var measurement: Measurement? = null
        try {
            measurement = service.getMeasurement(
                NetworkDevice.fromDevice(device).getMainEndpointUrl()
            )
        } catch (err: Exception) {
            device.available = false
        }
        return measurement
    }

    override suspend fun getHistory(device: Device): List<Measurement>? {
        try {
            return service.getHistory(
                NetworkDevice.fromDevice(device).getHistoryUrl()
            )
        } catch (_: Exception) {
        }
        return null
    }
}