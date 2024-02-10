package name.antonkonyshev.home.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface DevicePreferenceSchema {
    @GET
    suspend fun getPreferences(@Url url: String): DevicePreferenceModel

    @FormUrlEncoded
    @POST
    suspend fun setPreferences(@Url url: String, @Body preferences: DevicePreferenceModel)
}

object DevicePreferenceAPI : DevicePreferenceService {
    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val service: DevicePreferenceSchema by lazy {
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost").build().create(DevicePreferenceSchema::class.java)
    }

    override suspend fun getPreferences(device: Device): DevicePreference {
        try {
            val preference = service.getPreferences(
                NetworkDevice.fromDevice(device).getPreferenceUrl())
            preference.device = device
            return preference.toDevicePreference()
        } catch (_: Exception) {
            return DevicePreference()
        }
    }

    override suspend fun setPreferences(device: Device) {
        TODO("Not yet implemented")
    }
}
