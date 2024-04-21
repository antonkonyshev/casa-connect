package com.github.antonkonyshev.casaconnect.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.repository.CameraApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
interface CameraApiSchema {

    @Streaming
    @GET
    fun fetchPicture(@Url url: String): Call<ResponseBody>

}

@Singleton
class CameraApiClientImpl @Inject constructor(
    private val schema: CameraApiSchema
) : CameraApiClient {

    override suspend fun loadPicture(
        device: Device
    ): Bitmap? = suspendCancellableCoroutine { continuation ->
        val call: Call<ResponseBody> = schema.fetchPicture(
            NetworkDevice.fromDevice(device).getMainEndpointUrl()
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (
                    response.isSuccessful && response.body() != null && response.headers()
                        .get("Content-Type") == "image/bmp"
                ) {
                    try {
                        continuation.resume(
                            BitmapFactory.decodeStream(
                                response.body()!!.byteStream()
                            )
                        )
                        return
                    } catch (_: Exception) {
                    }
                }
                continuation.resume(null)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                continuation.resume(null)
            }
        })
    }

}