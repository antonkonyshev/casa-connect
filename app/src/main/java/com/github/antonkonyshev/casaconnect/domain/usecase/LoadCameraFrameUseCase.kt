package com.github.antonkonyshev.casaconnect.domain.usecase

import android.graphics.Bitmap
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.CameraApiClient
import javax.inject.Inject

class LoadCameraFrameUseCase @Inject constructor(
    private val client: CameraApiClient
) {
    suspend operator fun invoke(device: Device): Bitmap? {
        return client.loadFrame(device)
    }
}