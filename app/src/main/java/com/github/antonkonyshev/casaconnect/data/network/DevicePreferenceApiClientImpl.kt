package com.github.antonkonyshev.casaconnect.data.network

import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.domain.repository.DevicePreferenceApiClient
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface DevicePreferenceSchema {
    @GET
    suspend fun getPreferences(@Url url: String): DevicePreferenceModel

    @FormUrlEncoded
    @POST
    suspend fun setPreferences(@Url url: String, @FieldMap params: HashMap<String, String>)
}

@Singleton
class DevicePreferenceApiClientImpl @Inject constructor(
    private val service: DevicePreferenceSchema
) : DevicePreferenceApiClient {

    override suspend fun getPreferences(device: Device): DevicePreference? {
        try {
            val preference = service.getPreferences(
                NetworkDevice.fromDevice(device).getPreferenceUrl()
            )
            preference.device = device
            return preference.toDevicePreference()
        } catch (_: Exception) {
            return null
        }
    }

    override suspend fun setPreferences(preference: DevicePreference): Boolean {
        try {
            service.setPreferences(
                NetworkDevice.fromDevice(preference.device!!).getPreferenceUrl(),
                DevicePreferenceModel.fromDevicePreference(preference).toHashMap()
            )
            return true
        } catch (_: Exception) {}
        return false
    }
}
