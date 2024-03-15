package com.github.antonkonyshev.casaconnect.domain.entity

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import kotlinx.parcelize.Parcelize
import java.net.InetAddress

@Parcelize
open class Device(
    open val id: String,
    open val service: String,
    open val name: String,
    open val sensors: List<String>,
    open val ip: InetAddress? = null,
    open var available: Boolean = false
) : Parcelable {
    companion object {
        const val METEO_SENSOR_SERVICE_TYPE = "meteo"

        val serviceIcon = Icons.Default.Thermostat
    }
}

