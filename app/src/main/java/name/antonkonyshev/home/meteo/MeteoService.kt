package name.antonkonyshev.home.meteo

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface MeteoService {
    @GET
    suspend fun getMeasurement(@Url url: String) : Measurement

    @GET
    suspend fun getHistory(@Url url: String) : List<Measurement>
}

object MeteoAPI {
    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val service: MeteoService by lazy {
        Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost").build().create(MeteoService::class.java)
    }
}