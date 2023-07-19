package name.antonkonyshev.home.meteo

data class Measurement(
    val timestamp: Long = 0L,
    val temperature: Float? = null,
    val pressure: Float? = null,
    val altitude: Float? = null,
    val pollution: Float? = null,
)
