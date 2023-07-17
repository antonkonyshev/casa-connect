package name.antonkonyshev.home.devices

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.InetAddress
import java.util.Date

@Entity(tableName = "device")
open class Device (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "service")
    var service: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "sensors")
    var sensors: List<String> = emptyList(),

    @Transient
    @ColumnInfo(name = "ip")
    var ip: InetAddress? = null,

    @Transient
    @ColumnInfo(name = "available")
    var available: Boolean = true,
) {
    constructor() : this("", "", "", emptyList<String>(), null, true)

    @Transient
    @ColumnInfo(name = "ordering")
    var ordering: Int = 0

    @Transient
    @ColumnInfo(name = "updated", defaultValue = "CURRENT_TIMESTAMP")
    var updated: Date = Date()

    open fun copy(
        id: String = this.id, service: String = this.service, name: String = this.name,
        sensors: List<String> = this.sensors.toList(), ip: InetAddress? = this.ip,
        available: Boolean? = this.available
    ) = Device(id, service, name, sensors, ip, available = true)

    fun updateState(ip: InetAddress? = null, available: Boolean = true) {
        if (ip is InetAddress && this.ip?.hostAddress != ip.hostAddress) {
            this.ip = ip
        }
        this.available = available
    }

    companion object {
        val serviceIcon = Icons.Default.Thermostat
    }
}