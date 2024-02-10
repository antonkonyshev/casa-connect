package name.antonkonyshev.home.data.network

import com.squareup.moshi.Json
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference

data class DevicePreferenceModel(
    @Json(name = "high_pollution_value")
    override var highPollution: Int = 0,

    @Json(name = "min_thermometer_temperature")
    override var minTemperature: Int = 0,

    @Json(name = "max_thermometer_temperature")
    override var maxTemperature: Int = 0,

    @Json(name = "measurement_period")
    override var measurementPeriod: Int = 0,

    @Json(name = "time_sync_period")
    override var timeSyncPeriod: Int = 0,

    @Json(name = "history_length")
    override var historyLength: Int = 0,

    @Json(name = "history_record_period")
    override var historyRecordPeriod: Int = 0,

    @Json(name = "wifi_ssid")
    override var wifiSsid: String = "",

    @Transient
    override var device: Device? = null
): DevicePreference(highPollution, minTemperature, maxTemperature, measurementPeriod, timeSyncPeriod, historyLength, historyRecordPeriod, wifiSsid, device) {
    fun toDevicePreference(): DevicePreference {
        return this
    }

    companion object {
        fun fromDevicePreference(preference: DevicePreference): DevicePreferenceModel {
            return preference as DevicePreferenceModel
        }
    }
}
