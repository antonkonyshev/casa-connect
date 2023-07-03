package name.antonkonyshev.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.SensorValue
import name.antonkonyshev.home.ui.theme.HomeTheme
import name.antonkonyshev.home.utils.DevicePosture
import name.antonkonyshev.home.utils.isBookPosture
import name.antonkonyshev.home.utils.isSeparating
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.time.LocalDateTime
import java.time.ZoneOffset

// TODO: Discovery of the available services in the local network
// TODO: Storing info about local services and checking availability of them on start up or resume
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        HiddenApiBypass.addHiddenApiExemptions("L")
        super.onCreate(savedInstanceState)

        val devicePostureFlow = WindowInfoTracker
            .getOrCreate(this)
            .windowLayoutInfo(this)
            .flowWithLifecycle(this.lifecycle)
            .map { layoutInfo ->
                val foldingFeature = layoutInfo
                    .displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
                when {
                    isBookPosture(foldingFeature) ->
                        DevicePosture.BookPosture(foldingFeature?.bounds)
                    isSeparating(foldingFeature) ->
                        DevicePosture.Separating(foldingFeature?.bounds, foldingFeature?.orientation)
                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )

        setContent {
            HomeTheme {
                val uiState = viewModel.uiState.collectAsState().value
                val windowSize = calculateWindowSizeClass(activity = this)
                val devicePosture = devicePostureFlow.collectAsState().value
                HomeApp(uiState, windowSize.widthSizeClass, devicePosture)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeAppPreview() {
    HomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            ),
            windowSize = WindowWidthSizeClass.Compact,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun HomeAppPreviewTablet() {
    HomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            ),
            windowSize = WindowWidthSizeClass.Medium,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun HomeAppPreviewDesktop() {
    HomeTheme {
        HomeApp(
            homeUIState = HomeUIState(
                measurement = Measurement(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    temperature = SensorValue(value = 24.61F, unit = "C"),
                    pressure = SensorValue(value = 743.58F, unit = "mmHg"),
                    altitude = SensorValue(value = 146.35F, unit = "m")
                )
            ),
            windowSize = WindowWidthSizeClass.Expanded,
            foldingDevicePosture = DevicePosture.NormalPosture
        )
    }
}
