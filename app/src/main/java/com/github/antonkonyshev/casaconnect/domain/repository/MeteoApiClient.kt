package com.github.antonkonyshev.casaconnect.domain.repository

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.Measurement

interface MeteoApiClient {
    suspend fun getMeasurement(device: Device): Measurement?
    suspend fun getHistory(device: Device): List<Measurement>?
}