package name.antonkonyshev.home.meteo

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface MeteoService {
    @GET
    suspend fun getMeasurement(@Url url: String) : Measurement

    @GET
    suspend fun getHistory(@Url url: String) : List<Measurement>
}

object MeteoAPI {
    val retrofitService: MeteoService by lazy { retrofit.create(MeteoService::class.java) }
}