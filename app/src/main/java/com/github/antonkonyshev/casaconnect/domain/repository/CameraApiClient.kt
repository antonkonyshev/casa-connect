package com.github.antonkonyshev.casaconnect.domain.repository

import android.graphics.Bitmap
import com.github.antonkonyshev.casaconnect.domain.entity.Device

interface CameraApiClient {
    suspend fun loadFrame(device: Device): Bitmap?
}
