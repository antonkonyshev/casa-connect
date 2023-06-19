package name.antonkonyshev.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.MeteoApi
import name.antonkonyshev.home.ui.theme.CoderLabHomeTheme
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date

class MainActivity : ComponentActivity() {
    private val _measurement = Measurement()
    private val measurementState = mutableStateOf(_measurement)
    private val _history: List<Measurement> = listOf()
    private val historyState = mutableStateOf(_history)
    private var lastMeasurementUpdate: LocalDateTime? = null
    private var lastHistoryUpdate: LocalDateTime? = null
    private var _scope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        HiddenApiBypass.addHiddenApiExemptions("L")
        super.onCreate(savedInstanceState)
        setContent {
            CoderLabHomeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingText()
                }
            }
        }
    }

    @Composable
    fun GreetingText(modifier: Modifier = Modifier) {
        val measurement by measurementState
        val history by historyState
        // TODO: Make Material UI and theme
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = modifier.padding(8.dp),
        ) {
            Row (
                modifier = modifier.padding(8.dp),
            ) {
                Text(
                    text = "Time: ",
                    fontSize = 28.sp,
                )
                Text(
                    text = if (measurement.timestamp > 1687100000L) {
                        SimpleDateFormat("HH:MM:SS").format(
                            Date(measurement.timestamp * 1000))
                    } else "",
                    fontSize = 28.sp,
                )
            }
            Row (
                modifier = modifier.padding(8.dp),
            ) {
                Text(
                    text = "Temperature: ",
                    fontSize = 28.sp,
                )
                Text(
                    text = if (measurement.temperature.value >= -273F) {
                        "${measurement.temperature.value} ${measurement.temperature.unit}"
                    } else "",
                    fontSize = 28.sp,
                )
            }
            Row (
                modifier = modifier.padding(8.dp),
            ) {
                Text(
                    text = "Pressure: ",
                    fontSize = 28.sp,
                )
                Text(
                    text = if (measurement.pressure.value >= 0) {
                        "${measurement.pressure.value} ${measurement.pressure.unit}"
                    } else "",
                    fontSize = 28.sp,
                )
            }
            Row (
                modifier = modifier.padding(8.dp),
            ) {
                Text(
                    text = "Altitude: ",
                    fontSize = 28.sp,
                )
                Text(
                    text = if (measurement.altitude.value >= 0) {
                        "${measurement.altitude.value} ${measurement.altitude.unit}"
                    } else "",
                    fontSize = 28.sp,
                )
            }
            // TODO: Draw a chart with indicator value changing over time
            Row (
                modifier = modifier.padding(8.dp),
            ) {
                Text(
                    text = "History contains ${history.size} entries",
                    fontSize = 28.sp,
                )
            }
        }
        // TODO: set indicators and measurement history updating by a timer
        if ((lastMeasurementUpdate == null) || (Duration.between(
                lastMeasurementUpdate,
                LocalDateTime.now()
            ).seconds >= 60L)
        ) {
            _scope = rememberCoroutineScope()
            _scope?.launch {
                measurementState.value = MeteoApi.retrofitService.getMeasurement()
                lastHistoryUpdate = null
            }
            lastMeasurementUpdate = LocalDateTime.now()
        }
        if (lastHistoryUpdate == null || (Duration.between(
                lastHistoryUpdate,
                LocalDateTime.now()
            ).seconds >= 60L)
        ) {
            _scope = rememberCoroutineScope()
            _scope?.launch {
                historyState.value = MeteoApi.retrofitService.getHistory()
            }
            lastHistoryUpdate = LocalDateTime.now()
        }
    }
}
