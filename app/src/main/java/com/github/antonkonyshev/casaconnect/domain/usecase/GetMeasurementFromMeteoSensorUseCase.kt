package com.github.antonkonyshev.casaconnect.domain.usecase

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.Measurement
import com.github.antonkonyshev.casaconnect.domain.repository.MeteoApiClient
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