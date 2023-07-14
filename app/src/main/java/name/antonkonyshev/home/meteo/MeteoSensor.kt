package name.antonkonyshev.home.meteo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import name.antonkonyshev.home.devices.Device
import java.net.Inet4Address
import java.net.InetAddress

class MeteoSensor(
    id: String,
    service: String,
    name: String,
    sensors: List<String>,
    ip: InetAddress,
    available: Boolean = false
) : Device(id, service, name, sensors, available) {
    private val _measurement = MutableStateFlow(Measurement())
    val measurement: StateFlow<Measurement> = _measurement

    private val _history = MutableStateFlow<List<Measurement>>(emptyList())
    val history: StateFlow<List<Measurement>> = _history

    companion object {
        fun fromDevice(device: Device, ip: InetAddress, available: Boolean = false) = MeteoSensor(
            device.id, device.service, device.name, device.sensors, ip, available)
    }
}
