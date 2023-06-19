package name.antonkonyshev.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.SensorValue
import name.antonkonyshev.home.ui.theme.CoderLabHomeTheme
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.time.LocalDateTime
import java.time.ZoneOffset

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        HiddenApiBypass.addHiddenApiExemptions("L")
        super.onCreate(savedInstanceState)

        setContent {
            CoderLabHomeTheme {
                val uiState = viewModel.uiState.collectAsState().value
                HomeApp(uiState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeAppPreview() {
    CoderLabHomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun HomeAppPreviewTablet() {
    CoderLabHomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun HomeAppPreviewDesktop() {
    CoderLabHomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            )
        )
    }
}
