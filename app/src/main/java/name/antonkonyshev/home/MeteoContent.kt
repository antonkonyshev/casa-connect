package name.antonkonyshev.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import name.antonkonyshev.home.meteo.Measurement
import name.antonkonyshev.home.meteo.SensorValue

@Composable
fun MeteoContent (
    homeUIState: HomeUIState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
                .height(80.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meteostation",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .weight(3f)
                    .padding(horizontal = 40.dp)
            )
        }

        // TODO: Need for scroll
        // TODO: Replace hardcoded strings with resources and translate them
        // TODO: Add chart representing changing of values over time
        // TODO: Add periodical updating of measurements and updating on resume
        // TODO: Add manual updating of measurements
        SensorValueListItem(
            label = "Temperature",
            icon = Icons.Default.Thermostat,
            sensor = homeUIState.measurement.temperature,
            modifier = modifier
        )
        SensorValueListItem(
            label = "Pressure",
            icon = Icons.Default.TireRepair,
            sensor = homeUIState.measurement.pressure,
            modifier = modifier
        )
        SensorValueListItem(
            label = "Altitude",
            icon = Icons.Default.Landscape,
            sensor = homeUIState.measurement.altitude,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            Icon(imageVector = icon, contentDescription = label, modifier = modifier.weight(1f))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(3f)
            )
            Text(
                text = if (sensor.value > -300) "${sensor.value} ${sensor.unit}" else "",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(2f),
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
