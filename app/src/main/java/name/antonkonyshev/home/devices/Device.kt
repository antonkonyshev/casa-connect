package name.antonkonyshev.home.devices

open class Device (
    val id: String,
    val service: String,
    val name: String,
    val sensors: List<String>,
    var available: Boolean?,
)