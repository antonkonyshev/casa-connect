package name.antonkonyshev.home.domain.entity

import name.antonkonyshev.home.data.database.DeviceModel
import java.net.InetAddress

class MeteoSensor(
    id: String,
    service: String,
    name: String,
    sensors: List<String>,
    ip: InetAddress? = null,
    available: Boolean = false
) : DeviceModel(id, service, name, sensors, ip, available) {

    /*
    private val _measurement = MutableStateFlow(Measurement())
    val measurement: StateFlow<Measurement> = _measurement

    private val _history = MutableStateFlow<List<Measurement>>(emptyList())
    val history: StateFlow<List<Measurement>> = _history
    */

    companion object {
        fun fromDevice(device: DeviceModel, ip: InetAddress, available: Boolean = false) = MeteoSensor(
            device.id, device.service, device.name, device.sensors, ip, available)
    }
}
