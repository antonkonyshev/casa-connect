package com.github.antonkonyshev.casaconnect.domain.entity

import android.hardware.Sensor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Masks
import androidx.compose.material.icons.filled.SensorDoor
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.VideoCameraBack
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import java.net.InetAddress
import kotlin.reflect.full.isSubclassOf

class Device(
    val id: String,
    val service: String,
    var name: String,
    val sensors: List<String>,
    val ip: InetAddress? = null,
    var available: Boolean = false
) {
    companion object {
    }

    val type: DeviceType by lazy {
        DeviceType::class.nestedClasses.filter { it.isFinal && it.isSubclassOf(DeviceType::class) }
            .map { it.objectInstance as DeviceType }
            .firstOrNull { it.service == service }
            ?: DeviceType.UnknownDeviceType
    }

    val sensorTypes: List<SensorType> by lazy {
        SensorType::class.nestedClasses.filter { it.isFinal && it.isSubclassOf(SensorType::class) }
            .map { it.objectInstance as SensorType }
            .filter { it.sensor in sensors }
    }

    val sensorTypesExcludingMain: List<SensorType> by lazy {
        sensorTypes.filter { it.icon != type.icon }
    }
}

sealed class DeviceType(val service: String, val icon: ImageVector) {
    object MeteoDeviceType : DeviceType("meteo", Icons.Default.Thermostat)
    object DoorDeviceType : DeviceType("door", Icons.Default.SensorDoor)
    object UnknownDeviceType : DeviceType("unknown", Icons.Default.DeviceUnknown)
}

sealed class SensorType(val sensor: String, val icon: ImageVector, val ordering: Short) {
    object TemperatureSensor : SensorType("temperature", Icons.Default.Thermostat, 0)
    object CameraSensor : SensorType("video", Icons.Outlined.VideoCameraBack, 1)
    object PictureSensor : SensorType("picture", Icons.Outlined.CameraAlt, 2)
    object PollutionSensor : SensorType("pollution", Icons.Outlined.Masks, 3)
    object PressureSensor : SensorType("pressure", Icons.Outlined.Compress, 4)
    object HumiditySensor : SensorType("humidity", Icons.Outlined.WaterDrop, 5)
    object AltitudeSensor : SensorType("altitude", Icons.Outlined.Landscape, 6)
}