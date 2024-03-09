package name.antonkonyshev.home.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.Inet4Address

class DevicePreferenceApiClientImplTest {
    private val server = MockWebServer()
    private val schema = Retrofit.Builder().addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        )
    ).baseUrl(server.url("").toString()).client(
        OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder().url(
                    request.url.newBuilder().port(server.port).build()
                ).build()
                return@addInterceptor chain.proceed(request)
            }
        }.build()
    ).build().create(DevicePreferenceSchema::class.java)
    private val client = DevicePreferenceApiClientImpl(schema)

    @Test
    fun getPreferences() {
        server.enqueue(
            MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"high_pollution_value\":20,\"min_thermometer_temperature\":14,\"max_thermometer_temperature\":24,\"measurement_period\":15,\"time_sync_period\":1800,\"history_length\":50,\"history_record_period\":1800,\"wifi_ssid\":\"\"}")
        )
        runBlocking {
            val prefs = client.getPreferences(
                Device(
                    "room-1",
                    "meteo",
                    "Room",
                    listOf("temperature", "pressure", "pollution"),
                    Inet4Address.getByName(server.hostName),
                    true
                )
            )
            assertEquals(20, prefs?.highPollution)
            assertEquals(14, prefs?.minTemperature)
            assertEquals(24, prefs?.maxTemperature)
            assertEquals(15, prefs?.measurementPeriod)
            assertEquals(1800, prefs?.timeSyncPeriod)
            assertEquals(50, prefs?.historyLength)
            assertEquals(1800, prefs?.historyRecordPeriod)
        }
    }

    @Test
    fun setPreferences_invalidResponse() {
        server.enqueue(
            MockResponse().setResponseCode(400)
        )
        runBlocking {
            assertFalse(
                client.setPreferences(
                    DevicePreference(
                        20, 14, 24, 15,
                        1800, 50, 1800, "",
                        Device(
                            "room-1", "meteo", "Room",
                            listOf("temperature", "pressure", "pollution"),
                            Inet4Address.getByName(server.hostName),
                            true
                        )
                    )
                )
            )
        }
    }

    @Test
    fun getPreferences_invalidResponse() {
        server.enqueue(
            MockResponse().setResponseCode(400)
        )
        runBlocking {
            assertNull(
                client.getPreferences(
                    Device(
                        "room-1",
                        "meteo",
                        "Room",
                        listOf("temperature", "pressure", "pollution"),
                        Inet4Address.getByName(server.hostName),
                        true
                    )
                )
            )
        }
    }
}