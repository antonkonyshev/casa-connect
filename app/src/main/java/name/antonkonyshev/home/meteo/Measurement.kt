package name.antonkonyshev.home.meteo

data class SensorValue(
    val value: Float = -300F,
    val unit: String = "",
)

data class Measurement(
    val timestamp: Long = 0L,
    val temperature: SensorValue = SensorValue(),
    val pressure: SensorValue = SensorValue(),
    val altitude: SensorValue = SensorValue(),
)