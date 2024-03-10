package name.antonkonyshev.home.data.network

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.domain.repository.MeteoApiClient
import retrofit2.http.GET
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

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
        } catch (_: Exception) {
        }
        return null
    }
}