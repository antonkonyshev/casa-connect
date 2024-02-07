package name.antonkonyshev.home.settings

import com.squareup.moshi.Json
import name.antonkonyshev.home.devices.Device

data class DevicePreference (
    @Json(name="high_pollution_value")
    var highPollution: Int,

    @Json(name="min_thermometer_temperature")
    var minTemperature: Int,

    @Json(name="max_thermometer_temperature")
    var maxTemperature: Int,

    @Json(name="measurement_period")
    var measurementPeriod: Int,

    @Json(name="time_sync_period")
    var timeSyncPeriod: Int,

    @Json(name="history_length")
    var historyLength: Int,

    @Json(name="history_record_period")
    var historyRecordPeriod: Int,

    @Json(name="wifi_ssid")
    var wifiSsid: String,
) {
    @Transient
    var device: Device? = null

    @Transient
    @Json(name="wifi_password")
    var wifiPassword: String = ""

    constructor() : this(
        0, 0, 0, 0,
        0, 0, 0, "",
    )
}