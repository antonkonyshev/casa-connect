package name.antonkonyshev.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.SensorValue
import name.antonkonyshev.home.utils.getLocalUnits

@Composable
fun MeteoContent (
    homeUIState: HomeUIState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(start = 40.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.meteostation),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
            )
            Button(
                onClick = { viewModel.observeMeasurement() },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = stringResource(R.string.refresh),
                    modifier = Modifier
                        .padding(0.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                )
            }
        }

        // TODO: Localization for sensor value units
        // TODO: Add chart representing changing of values over time
        @Suppress("DEPRECATION")
        SwipeRefresh(
            state = rememberSwipeRefreshState(homeUIState.loading),
            onRefresh = { viewModel.observeMeasurement() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(ScrollState(0))
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                SensorValueListItem(
                    label = stringResource(R.string.temperature),
                    icon = Icons.Default.Thermostat,
                    sensor = homeUIState.measurement.temperature,
                    modifier = modifier
                )
                SensorValueListItem(
                    label = stringResource(R.string.pressure),
                    icon = Icons.Default.TireRepair,
                    sensor = homeUIState.measurement.pressure,
                    modifier = modifier
                )
                SensorValueListItem(
                    label = stringResource(R.string.altitude),
                    icon = Icons.Default.Landscape,
                    sensor = homeUIState.measurement.altitude,
                    modifier = modifier
                )
                SensorValueListItem(
                    label = stringResource(R.string.history),
                    icon = Icons.Default.Timeline,
                    sensor = SensorValue(
                        if (homeUIState.history.size < 1) -300F
                        else homeUIState.history.size.toFloat(),
                        "records"
                    ),
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun SensorValueListItem(
    label: String,
    icon: ImageVector,
    sensor: SensorValue,
    modifier: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(end = 20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = if (sensor.value > -300) "${sensor.value} ${getLocalUnits(sensor.unit, LocalContext.current)}" else "",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MeteoContentPreview() {
    MeteoContent(
        homeUIState = HomeUIState(
            measurement = Measurement(
                timestamp = 10000000,
                temperature = SensorValue(value = 24.61F, unit = "C"),
                pressure = SensorValue(value = 743.35F, unit = "mmHg"),
                altitude = SensorValue(value = 146.18F, unit = "m")
            )
        )
    )
}

@Composable
@Preview(showBackground = true, widthDp = 700)
fun MeteoContentPreviewTablet() {
    MeteoContent(
        homeUIState = HomeUIState(
            measurement = Measurement(
                timestamp = 10000000,
                temperature = SensorValue(value = 24.61F, unit = "C"),
                pressure = SensorValue(value = 743.35F, unit = "mmHg"),
                altitude = SensorValue(value = 146.18F, unit = "m")
            )
        )
    )
}

@Composable
@Preview(showBackground = true, widthDp = 1000)
fun MeteoContentPreviewDesktop() {
    MeteoContent(
        homeUIState = HomeUIState(
            measurement = Measurement(
                timestamp = 10000000,
                temperature = SensorValue(value = 24.61F, unit = "C"),
                pressure = SensorValue(value = 743.35F, unit = "mmHg"),
                altitude = SensorValue(value = 146.18F, unit = "m")
            )
        )
    )
}
