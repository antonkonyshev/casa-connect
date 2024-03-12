package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.domain.repository.MeteoApiClient
import javax.inject.Inject

class GetMeasurementFromMeteoSensorUseCase @Inject constructor(
    private val client: MeteoApiClient
) {
    suspend fun getMeasurement(device: Device): Measurement? {
        return client.getMeasurement(device)
    }

    suspend fun getHistory(device: Device): List<Measurement>? {
        return client.getHistory(device)
    }
}