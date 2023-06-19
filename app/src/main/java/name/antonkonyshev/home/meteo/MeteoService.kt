package name.antonkonyshev.home.meteo

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// TODO: add local services discovery logic
private const val BASE_URL = "http://192.168.0.160"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MeteoService {
    @GET("/")
    suspend fun getMeasurement() : Measurement

    @GET("/history")
    suspend fun getHistory() : List<Measurement>
}

object MeteoApi {
    val retrofitService: MeteoService by lazy { retrofit.create(MeteoService::class.java) }
}