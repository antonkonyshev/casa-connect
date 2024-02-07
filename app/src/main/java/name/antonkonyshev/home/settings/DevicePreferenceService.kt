package name.antonkonyshev.home.settings

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface DevicePreferenceService {
    @GET
    suspend fun getPreferences(@Url url: String): DevicePreference

    @FormUrlEncoded
    @POST
    suspend fun setPreferences(@Url url: String, @Body preferences: DevicePreference)
}

object DevicePreferenceAPI {
    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val service: DevicePreferenceService by lazy {
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost").build().create(DevicePreferenceService::class.java)
    }
}
