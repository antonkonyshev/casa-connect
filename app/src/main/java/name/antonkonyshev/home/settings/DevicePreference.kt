package name.antonkonyshev.home.settings

import com.squareup.moshi.Json

data class DevicePreference (
    @Transient
    val deviceId: String,

    @Json(name="high_pollution_value")
    val highPollution: Int,

    @Json(name="min_thermometer_temperature")
    val minTemperature: Int,

    @Json(name="max_thermometer_temperature")
    val maxTemperature: Int,

    @Json(name="measurement_period")
    val measurementPeriod: Int,

    @Json(name="time_sync_period")
    val timeSyncPeriod: Int,

    @Json(name="history_length")
    val historyLength: Int,

    @Json(name="history_record_period")
    val historyRecordPeriod: Int,

    @Json(name="wifi_ssid")
    val wifiSsid: String,

    @Transient
    @Json(name="wifi_password")
    val wifiPassword: String,
)