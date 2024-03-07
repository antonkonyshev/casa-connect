package name.antonkonyshev.home.domain.usecase

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.domain.repository.MeteoService
import javax.inject.Inject

class GetMeasurementFromMeteoSensorUseCase @Inject constructor(
    private val meteoapi: MeteoService
) {
    suspend fun getMeasurement(device: Device): Measurement? {
        return meteoapi.getMeasurement(device)
    }

    suspend fun getHistory(device: Device): List<Measurement>? {
        return meteoapi.getHistory(device)
    }
}