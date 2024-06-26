package com.github.antonkonyshev.casaconnect.data.network

import com.squareup.moshi.Json
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference

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
) : DevicePreference(
    highPollution,
    minTemperature,
    maxTemperature,
    measurementPeriod,
    timeSyncPeriod,
    historyLength,
    historyRecordPeriod,
    wifiSsid,
    device
) {
    fun toDevicePreference(): DevicePreference {
        return this
    }

    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            "high_pollution_value" to highPollution.toString(),
            "min_thermometer_temperature" to minTemperature.toString(),
            "max_thermometer_temperature" to maxTemperature.toString(),
            "measurement_period" to measurementPeriod.toString(),
            "time_sync_period" to timeSyncPeriod.toString(),
            "history_length" to historyLength.toString(),
            "history_record_period" to historyRecordPeriod.toString(),
        )
    }

    companion object {
        fun fromDevicePreference(preference: DevicePreference): DevicePreferenceModel {
            return DevicePreferenceModel(
                preference.highPollution, preference.minTemperature, preference.maxTemperature,
                preference.measurementPeriod, preference.timeSyncPeriod, preference.historyLength,
                preference.historyRecordPeriod, preference.wifiSsid, preference.device
            )
        }
    }
}
