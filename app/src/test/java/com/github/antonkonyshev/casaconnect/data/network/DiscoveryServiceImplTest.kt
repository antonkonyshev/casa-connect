package com.github.antonkonyshev.casaconnect.data.network

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.github.antonkonyshev.casaconnect.data.database.AppDatabase
import com.github.antonkonyshev.casaconnect.data.database.DeviceModel
import com.github.antonkonyshev.casaconnect.data.database.DeviceRepositoryImpl
import com.github.antonkonyshev.casaconnect.data.network.DeviceModelSchema
import com.github.antonkonyshev.casaconnect.data.network.DiscoveryServiceImpl
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.Inet4Address

@RunWith(RobolectricTestRunner::class)
class DiscoveryServiceImplTest {
    private lateinit var repository: DeviceRepositoryImpl
    private lateinit var database: AppDatabase
    private lateinit var service: DiscoveryServiceImpl

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
    ).build().create(DeviceModelSchema::class.java)

    @Before
    fun setUp() {
        runBlocking {
            database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), AppDatabase::class.java
            ).allowMainThreadQueries().build()
            repository = DeviceRepositoryImpl(database)
            service = DiscoveryServiceImpl(schema, repository)
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            database.close()
        }
    }

    @Test
    fun discoverDevices() {
        server.enqueue(
            MockResponse().addHeader("Content-Type", "application/json")
                .setBody("{\"service\":\"meteo\",\"name\":\"Room\",\"id\":\"room-1\",\"sensors\":[\"temperature\",\"pressure\",\"altitude\",\"pollution\"]}")
        )
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName(server.hostName), false
                )
            )
            val deviceIp = if (
                server.hostName == "192.168.0.102"
            ) "192.168.0.103" else "192.168.0.102"
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "meteo", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName(deviceIp), false
                )
            )
            service.discoverDevices()
            service.checkIpAddress(deviceIp)
            service.checkIpAddress(server.hostName)
            delay(1000L)
        }
        runBlocking {
            val devices = repository.allDevices.first()
            assertEquals(devices.size, 2)
            assertEquals(devices[0].id, "room-1")
            assertEquals(devices[0].ip!!.hostName, server.hostName)
            assertTrue(devices[0].available)
            assertEquals(devices[1].id, "kitchen-1")
            assertFalse(devices[1].available)
        }
    }

    @Test
    fun discoverDevices_invalidResponse() {
        runBlocking {
            repository.updateStateOrCreate(
                DeviceModel(
                    "room-1", "meteo", "Room",
                    listOf("temperature", "pollution"),
                    Inet4Address.getByName(server.hostName), false
                )
            )
            val deviceIp = if (
                server.hostName == "192.168.0.102"
            ) "192.168.0.103" else "192.168.0.102"
            repository.updateStateOrCreate(
                DeviceModel(
                    "kitchen-1", "meteo", "Kitchen",
                    listOf("temperature", "pressure"),
                    Inet4Address.getByName(deviceIp), false
                )
            )
            server.shutdown()
            service.discoverDevices()
            service.checkIpAddress(deviceIp)
            service.checkIpAddress(server.hostName)
            val devices = repository.allDevices.first()
            assertEquals(devices.size, 2)
            assertFalse(devices[0].available)
            assertFalse(devices[1].available)
        }
    }
}