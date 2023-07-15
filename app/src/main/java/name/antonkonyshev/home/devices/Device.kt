package name.antonkonyshev.home.devices

import java.net.InetAddress

open class Device (
    val id: String,
    val service: String,
    val name: String,
    val sensors: List<String>,

    @Transient
    var ip: InetAddress? = null,

    @Transient
    var available: Boolean? = false,
) {
    open fun copy(
        id: String = this.id, service: String = this.service, name: String = this.name,
        sensors: List<String> = this.sensors.toList(), ip: InetAddress? = this.ip,
        available: Boolean? = this.available
    ) = Device(id, service, name, sensors, ip, available == true)

    fun updateState(ip: InetAddress? = null, available: Boolean? = true) {
        if (ip is InetAddress && this.ip?.hostAddress != ip.hostAddress) {
            this.ip = ip
        }
        this.available = available
    }
}