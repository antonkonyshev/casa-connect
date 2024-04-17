package com.github.antonkonyshev.casaconnect.domain.entity

import java.net.InetAddress

data class MeteoSensor(
    override val id: String,
    override var name: String,
    override val sensors: List<String>,
    override val ip: InetAddress? = null,
    override var available: Boolean = false
) : Device(id, service = METEO_SENSOR_SERVICE_TYPE, name, sensors, ip, available) {
//    companion object {
//        val serviceIcon = Icons.Default.Thermostat
//    }
}

