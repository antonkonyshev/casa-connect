package com.github.antonkonyshev.casaconnect.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.InetAddress

@Parcelize
open class Device(
    open val id: String,
    open val service: String,
    open var name: String,
    open val sensors: List<String>,
    open val ip: InetAddress? = null,
    open var available: Boolean = false
) : Parcelable {
    companion object {
        const val METEO_SENSOR_SERVICE_TYPE = "meteo"
    }
}

