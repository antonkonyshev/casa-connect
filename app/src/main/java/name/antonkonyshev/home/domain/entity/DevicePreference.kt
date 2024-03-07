package name.antonkonyshev.home.domain.entity

open class DevicePreference(
    open var highPollution: Int = 0,
    open var minTemperature: Int = 0,
    open var maxTemperature: Int = 0,
    open var measurementPeriod: Int = 0,
    open var timeSyncPeriod: Int = 0,
    open var historyLength: Int = 0,
    open var historyRecordPeriod: Int = 0,
    open var wifiSsid: String = "",
    open var device: Device? = null
)
