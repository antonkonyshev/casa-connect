package name.antonkonyshev.home.domain.repository

import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement

interface MeteoService {
    suspend fun getMeasurement(device: Device): Measurement?
    suspend fun getHistory(device: Device): List<Measurement>?
}