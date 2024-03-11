package name.antonkonyshev.home.presentation.meteo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import name.antonkonyshev.home.R
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.Measurement
import name.antonkonyshev.home.presentation.LocalizationUtils
import name.antonkonyshev.home.presentation.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoScreen(
    devices: List<Device>,
    measurements: Map<String, State<Measurement>>,
    histories: Map<String, State<List<Measurement>>>,
    onDrawerClicked: () -> Unit = {},
    onRefresh: () -> Unit = {},
    uiState: UiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(start = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_label)
                )
            }
            Text(
                text = stringResource(R.string.meteostation),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(start = 7.dp)
            )
        }

        val refreshingState = rememberPullRefreshState(uiState.loading, onRefresh)
        Box(modifier = Modifier.pullRefresh(refreshingState)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(devices) { device ->
                    ListItem(
                        headlineContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = LocalizationUtils.localizeDefaultServiceName(
                                        device.name,
                                        LocalContext.current
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .padding(top = 10.dp, bottom = 10.dp)
                                        .weight(1f)
                                )
                                if (device.available) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = "Online",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.SensorsOff,
                                        contentDescription = "Offline",
                                        tint = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                            }
                        },
                        supportingContent = {
                            if (uiState.loading) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.loading),
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                if (measurements[device.id] is State<Measurement>) {
                                    Column {
                                        Row {
                                            SensorValueListItem(
                                                label = stringResource(R.string.temperature),
                                                icon = Icons.Outlined.Thermostat,
                                                sensorValue = measurements[device.id]!!.value.temperature,
                                                units = stringResource(R.string.c),
                                            )
                                        }
                                        Row {
                                            SensorValueListItem(
                                                label = stringResource(R.string.pressure),
                                                icon = Icons.Outlined.TireRepair,
                                                sensorValue = measurements[device.id]!!.value.pressure,
                                                units = stringResource(R.string.mmhg),
                                            )
                                        }
                                        Row {
                                            SensorValueListItem(
                                                label = stringResource(R.string.pollution),
                                                icon = Icons.Outlined.Masks,
                                                sensorValue = measurements[device.id]!!.value.pollution,
                                                units = stringResource(R.string.mg_m3),
                                            )
                                        }
                                        Row {
                                            SensorValueListItem(
                                                label = stringResource(R.string.altitude),
                                                icon = Icons.Outlined.Landscape,
                                                sensorValue = measurements[device.id]!!.value.altitude,
                                                units = stringResource(R.string.m),
                                            )
                                        }
                                        if (histories[device.id] is State<List<Measurement>>) {
                                            // TODO: Add chart representing changing of values over time
                                            Row {
                                                SensorValueListItem(
                                                    label = stringResource(R.string.history),
                                                    icon = Icons.Outlined.Watch,
                                                    sensorValue = histories[device.id]!!.value.size.toFloat(),
                                                    units = stringResource(R.string.records).lowercase()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White.copy(alpha = 0.7F),
                        ),
                        modifier = Modifier
                            .padding(bottom = 18.dp)
                    )
                }
            }
            PullRefreshIndicator(
                uiState.loading, refreshingState, Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun SensorValueListItem(
    label: String,
    icon: ImageVector,
    sensorValue: Float?,
    units: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth(),
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
            text = if (sensorValue != null) "$sensorValue $units" else "",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier,
            textAlign = TextAlign.End
        )
    }
}
