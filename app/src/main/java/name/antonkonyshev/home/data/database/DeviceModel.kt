package name.antonkonyshev.home.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import name.antonkonyshev.home.domain.entity.Device
import java.net.InetAddress
import java.util.Date

@Entity(tableName = "device")
class DeviceModel(
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

    fun toDevice(): Device = Device(id, service, name, sensors, ip, available)

    companion object {
        fun fromDevice(device: Device): DeviceModel = DeviceModel(
            device.id, device.service, device.name, device.sensors, device.ip, device.available
        )
    }
}