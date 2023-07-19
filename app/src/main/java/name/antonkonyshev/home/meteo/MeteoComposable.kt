package name.antonkonyshev.home.meteo

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.TireRepair
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import name.antonkonyshev.home.R
import name.antonkonyshev.home.UiState
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.utils.localizeDefaultServiceName

@Composable
fun MeteoScreen(
    uiState: UiState,
    devices: List<Device>,
    viewModel: MeteoViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(start = 25.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                // TODO: get service info from the meteostation
                text = stringResource(R.string.meteostation),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
            )
            AnimatedVisibility(!uiState.loading) {
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
        }

        // TODO: Add chart representing changing of values over time
        @Suppress("DEPRECATION")
        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.loading),
            onRefresh = { viewModel.observeMeasurement() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(ScrollState(0))
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // TODO: Show on surface with secondary background
                    Text(
                        // TODO: get service info from the meteostation
                        text = localizeDefaultServiceName("Room", LocalContext.current),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                SensorValueListItem(
                    label = stringResource(R.string.temperature),
                    icon = Icons.Outlined.Thermostat,
                    sensorValue = viewModel.measurement.value.temperature,
                    units = stringResource(R.string.c),
                )
                SensorValueListItem(
                    label = stringResource(R.string.pressure),
                    icon = Icons.Outlined.TireRepair,
                    sensorValue = viewModel.measurement.value.pressure,
                    units = stringResource(R.string.mmhg),
                )
                SensorValueListItem(
                    label = stringResource(R.string.altitude),
                    icon = Icons.Outlined.Landscape,
                    sensorValue = viewModel.measurement.value.altitude,
                    units = stringResource(R.string.m),
                )
                SensorValueListItem(
                    label = stringResource(R.string.pollution),
                    icon = Icons.Outlined.Masks,
                    sensorValue = viewModel.measurement.value.pollution,
                    units = stringResource(R.string.mg_m3),
                )
            }
        }
    }
}

@Composable
fun SensorValueListItem(
    label: String,
    icon: ImageVector,
    sensorValue: Float?,
    units: String,
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
                text = if (sensorValue != null) "${sensorValue} ${units}" else "",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier,
                textAlign = TextAlign.End
            )
        }
    }
}
