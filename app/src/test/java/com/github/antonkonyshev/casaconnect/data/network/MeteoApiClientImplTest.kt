package com.github.antonkonyshev.casaconnect.data.network

import com.github.antonkonyshev.casaconnect.data.network.MeteoApiClientImpl
import com.github.antonkonyshev.casaconnect.data.network.MeteoServiceSchema
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.Inet4Address

class MeteoApiClientImplTest {
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
    ).build().create(MeteoServiceSchema::class.java)
    private val client = MeteoApiClientImpl(schema)

    @Test
    fun getMeasurement() {
        server.enqueue(
            MockResponse().addHeader("Content-Type", "application/json")
                .setBody("{\"timestamp\":1710055968,\"temperature\":27.50,\"pressure\":746.98,\"altitude\":145.55,\"pollution\":17.25}")
        )
        runBlocking {
            val measurement = client.getMeasurement(
                Device(
                    "room-1",
                    "meteo",
                    "Room",
                    listOf("temperature", "pressure", "pollution"),
                    Inet4Address.getByName(server.hostName),
                    true
                )
            )!!
            assertEquals(measurement.timestamp, 1710055968L)
            assertEquals(measurement.temperature, 27.50F)
            assertEquals(measurement.pressure, 746.98F)
            assertEquals(measurement.altitude, 145.55F)
            assertEquals(measurement.pollution, 17.25F)
        }
    }

    @Test
    fun getMeasurement_invalidResponse() {
        server.enqueue(MockResponse().setResponseCode(400))
        runBlocking {
            val measurement = client.getMeasurement(
                Device(
                    "room-1",
                    "meteo",
                    "Room",
                    listOf("temperature", "pressure", "pollution"),
                    Inet4Address.getByName(server.hostName),
                    true
                )
            )
            assertNull(measurement)
        }
    }

    @Test
    fun getHistory() {
        server.enqueue(
            MockResponse().addHeader("Content-Type", "application/json")
                .setBody(
                    "[{\"timestamp\":1710055968,\"temperature\":27.50,\"pressure\":746.98,\"altitude\":145.55,\"pollution\":17.25},{\"timestamp\":1710055983,\"temperature\":27.54,\"pressure\":746.95,\"altitude\":145.93,\"pollution\":19.43},{\"timestamp\":1710055998,\"temperature\":27.55,\"pressure\":746.95,\"altitude\":145.91,\"pollution\":18.11},{\"timestamp\":1710056013,\"temperature\":27.58,\"pressure\":746.95,\"altitude\":145.90,\"pollution\":21.11},{\"timestamp\":1710056028,\"temperature\":27.60,\"pressure\":746.93,\"altitude\":146.09,\"pollution\":21.46},{\"timestamp\":1710056043,\"temperature\":27.63,\"pressure\":746.95,\"altitude\":145.86,\"pollution\":21.70},{\"timestamp\":1710056058,\"temperature\":27.65,\"pressure\":746.94,\"altitude\":145.98,\"pollution\":22.11},{\"timestamp\":1710056073,\"temperature\":27.68,\"pressure\":746.97,\"altitude\":145.62,\"pollution\":19.17},{\"timestamp\":1710056088,\"temperature\":27.70,\"pressure\":746.93,\"altitude\":146.15,\"pollution\":22.36},{\"timestamp\":1710056103,\"temperature\":27.72,\"pressure\":746.97,\"altitude\":145.70,\"pollution\":22.62},{\"timestamp\":1710056118,\"temperature\":27.74,\"pressure\":746.94,\"altitude\":146.00,\"pollution\":22.89},{\"timestamp\":1710056133,\"temperature\":27.76,\"pressure\":746.95,\"altitude\":145.82,\"pollution\":22.97},{\"timestamp\":1710056148,\"temperature\":27.79,\"pressure\":746.93,\"altitude\":146.11,\"pollution\":22.85},{\"timestamp\":1710056163,\"temperature\":27.83,\"pressure\":746.94,\"altitude\":146.02,\"pollution\":23.07},{\"timestamp\":1710056178,\"temperature\":27.85,\"pressure\":746.92,\"altitude\":146.17,\"pollution\":23.17},{\"timestamp\":1710056193,\"temperature\":27.87,\"pressure\":746.93,\"altitude\":146.08,\"pollution\":23.33},{\"timestamp\":1710056208,\"temperature\":27.90,\"pressure\":746.93,\"altitude\":146.06,\"pollution\":23.35},{\"timestamp\":1710056223,\"temperature\":27.91,\"pressure\":746.93,\"altitude\":146.05,\"pollution\":23.21},{\"timestamp\":1710056238,\"temperature\":27.93,\"pressure\":746.91,\"altitude\":146.30,\"pollution\":23.37},{\"timestamp\":1710056253,\"temperature\":27.96,\"pressure\":746.93,\"altitude\":146.15,\"pollution\":23.63},{\"timestamp\":1710056268,\"temperature\":27.99,\"pressure\":746.94,\"altitude\":146.03,\"pollution\":23.59},{\"timestamp\":1710056283,\"temperature\":28.00,\"pressure\":746.92,\"altitude\":146.21,\"pollution\":23.63},{\"timestamp\":1710056298,\"temperature\":28.05,\"pressure\":746.93,\"altitude\":146.10,\"pollution\":23.80},{\"timestamp\":1710056313,\"temperature\":28.08,\"pressure\":746.93,\"altitude\":146.14,\"pollution\":23.92},{\"timestamp\":1710056328,\"temperature\":28.07,\"pressure\":746.94,\"altitude\":146.00,\"pollution\":24.21},{\"timestamp\":1710056343,\"temperature\":28.09,\"pressure\":746.93,\"altitude\":146.15,\"pollution\":20.64}]"
                )
        )
        runBlocking {
            val history = client.getHistory(
                Device(
                    "room-1",
                    "meteo",
                    "Room",
                    listOf("temperature", "pressure", "pollution"),
                    Inet4Address.getByName(server.hostName),
                    true
                )
            )!!
            assertEquals(history.size, 26)
            assertEquals(history[0].timestamp, 1710055968L)
            assertEquals(history[0].temperature, 27.50F)
            assertEquals(history[0].pressure, 746.98F)
            assertEquals(history[0].altitude, 145.55F)
            assertEquals(history[0].pollution, 17.25F)
            assertEquals(history[25].timestamp, 1710056343L)
            assertEquals(history[25].temperature, 28.09F)
            assertEquals(history[25].pressure, 746.93F)
            assertEquals(history[25].altitude, 146.15F)
            assertEquals(history[25].pollution, 20.64F)
        }
    }

    @Test
    fun getHistory_invalidResponse() {
        server.enqueue(
            MockResponse().setResponseCode(400)
        )
        runBlocking {
            val history = client.getHistory(
                Device(
                    "room-1",
                    "meteo",
                    "Room",
                    listOf("temperature", "pressure", "pollution"),
                    Inet4Address.getByName(server.hostName),
                    true
                )
            )
            assertNull(history)
        }
    }
}