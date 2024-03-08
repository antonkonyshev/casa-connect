package name.antonkonyshev.home.data.network

import android.util.Log
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import name.antonkonyshev.home.domain.repository.DevicePreferenceService
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

interface DevicePreferenceSchema {
    @GET
    suspend fun getPreferences(@Url url: String): DevicePreferenceModel

    @FormUrlEncoded
    @POST
    suspend fun setPreferences(@Url url: String, @FieldMap params: HashMap<String, String>)
}

@Singleton
class DevicePreferenceAPI @Inject constructor(
    private val service: DevicePreferenceSchema
) : DevicePreferenceService {

    override suspend fun getPreferences(device: Device): DevicePreference {
        try {
            val preference = service.getPreferences(
                NetworkDevice.fromDevice(device).getPreferenceUrl()
            )
            preference.device = device
            return preference.toDevicePreference()
        } catch (_: Exception) {
            return DevicePreference()
        }
    }

    override suspend fun setPreferences(preference: DevicePreference): Boolean {
        try {
            // TODO: Return result
            service.setPreferences(
                NetworkDevice.fromDevice(preference.device!!).getPreferenceUrl(),
                DevicePreferenceModel.fromDevicePreference(preference).toHashMap()
            )
            return true
        } catch (err: Exception) {}
        return false
    }
}
