package com.github.antonkonyshev.casaconnect.presentation.meteo

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.domain.entity.Measurement
import com.github.antonkonyshev.casaconnect.presentation.common.UiState
import com.github.antonkonyshev.casaconnect.presentation.common.collectAsEffect
import com.github.antonkonyshev.casaconnect.presentation.common.getActivity
import com.github.antonkonyshev.casaconnect.presentation.device.DeviceAvailabilityIcon
import com.github.antonkonyshev.casaconnect.presentation.device.DeviceEditIcon
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

@Composable
fun MeteoScreen(viewModel: MeteoViewModel = viewModel()) {
    val devices by viewModel.getDevicesByAttributeUseCase.getMeteoDevicesFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val measurements = viewModel.measurements.mapValues {
        it.value.measurementFlow.collectAsStateWithLifecycle()
    }
    val histories = viewModel.measurements.mapValues {
        it.value.historyFlow.collectAsStateWithLifecycle()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MeteoScreenContent(devices, measurements, histories, uiState, viewModel::observeMeasurement)

    LocalContext.current.getActivity()?.eventBus?.collectAsEffect {
        if (it.id == "ObserveMeasurement")
            viewModel.observeMeasurement()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MeteoScreenContent(
    devices: List<Device>,
    measurements: Map<String, State<Measurement>>,
    histories: Map<String, State<List<Measurement>>>,
    uiState: UiState,
    observeMeasurement: (Boolean) -> Unit = {}
) {
    val refreshingState = rememberPullRefreshState(uiState.loading, {
        observeMeasurement(true)
    })

    Box(
        modifier = Modifier
            .pullRefresh(refreshingState)
            .fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(devices) { device ->
                ListItem(headlineContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DeviceAvailabilityIcon(device)

                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(12.dp, 10.dp, 12.dp, 10.dp)
                                .weight(1f)
                        )

                        DeviceEditIcon(device)
                    }
                }, supportingContent = {
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
                                        label = stringResource(R.string.air_temperature),
                                        icon = Icons.Outlined.Thermostat,
                                        sensorValue = measurements[device.id]!!.value.temperature,
                                        units = stringResource(R.string.c),
                                        modifier = Modifier.padding(12.dp, 20.dp, 12.dp, 0.dp)
                                    )
                                }

                                if (
                                    histories[device.id] is State<List<Measurement>> &&
                                    measurements[device.id] is State<Measurement> &&
                                    (histories[device.id]?.value?.size ?: 0) > 2
                                ) {
                                    Row(
                                        Modifier
                                            .height(120.dp)
                                            .padding(end = 8.dp)) {
                                        MeasurementHistoryChart(
                                            histories[device.id]!!.value.map {
                                                it.temperature ?: 0f
                                            }.toList(),
                                            measurements[device.id]!!.value.temperature ?: 0f,
                                            label = stringResource(R.string.temperature),
                                            units = stringResource(R.string.c)
                                        )
                                    }
                                }

                                Row {
                                    SensorValueListItem(
                                        label = stringResource(R.string.air_pollution),
                                        icon = Icons.Outlined.Masks,
                                        sensorValue = measurements[device.id]!!.value.pollution,
                                        units = stringResource(R.string.mg_m3),
                                        modifier = Modifier.padding(12.dp, 30.dp, 12.dp, 0.dp)
                                    )
                                }

                                if (
                                    histories[device.id] is State<List<Measurement>> &&
                                    measurements[device.id] is State<Measurement> &&
                                    (histories[device.id]?.value?.size ?: 0) > 2
                                ) {
                                    Row(Modifier.height(120.dp)) {
                                        MeasurementHistoryChart(
                                            histories[device.id]!!.value
                                                .map { it.pollution ?: 0f }.toList(),
                                            measurements[device.id]!!
                                                .value.pollution ?: 0f,
                                            label = stringResource(R.string.pollution),
                                            units = stringResource(R.string.mg_m3),
                                            color = Color.Blue,
                                            granularity = 1f
                                        )
                                    }
                                }

                                Row {
                                    SensorValueListItem(
                                        label = stringResource(R.string.atmosphere_pressure),
                                        icon = Icons.Outlined.TireRepair,
                                        sensorValue = measurements[device.id]!!.value.pressure,
                                        units = stringResource(R.string.mmhg),
                                        modifier = Modifier.padding(12.dp, 30.dp, 12.dp, 0.dp)
                                    )
                                }

                                Row {
                                    SensorValueListItem(
                                        label = stringResource(R.string.altitude_above_sea_level),
                                        icon = Icons.Outlined.Landscape,
                                        sensorValue = measurements[device.id]!!.value.altitude,
                                        units = stringResource(R.string.m),
                                        modifier = Modifier.padding(12.dp, 30.dp, 12.dp, 20.dp)
                                    )
                                }
                            }
                        }
                    }
                }, colors = ListItemDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.7F),
                ), modifier = Modifier.padding(bottom = 18.dp)
                )
            }
        }
        PullRefreshIndicator(
            uiState.loading, refreshingState, Modifier.align(Alignment.TopCenter)
        )
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
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon, contentDescription = label, modifier = Modifier.padding(end = 20.dp)
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

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun MeteoScreenPreview() {
    CasaConnectTheme {
        MeteoScreenContent(
            devices = listOf(
                Device(
                    id = "room-1", service = "meteo", name = "Room",
                    sensors = listOf("temperature", "pressure", "altitude", "pollution")
                )
            ),
            measurements = mapOf(
                "room-1" to mutableStateOf(
                    Measurement(
                        10L, 22.53f, 738.52f, 184.32f,
                        15.21f
                    )
                )
            ),
            histories = mapOf(
                "room-1" to mutableStateOf(
                    listOf(
                        Measurement(
                            0L, 21.53f, 738.52f, 184.32f,
                            14.21f
                        ),
                        Measurement(
                            0L, 22.53f, 738.52f, 184.32f,
                            15.21f
                        ),
                        Measurement(
                            0L, 23.53f, 738.52f, 184.32f,
                            16.21f
                        ),
                        Measurement(
                            0L, 22.53f, 738.52f, 184.32f,
                            15.21f
                        ),
                    )
                )
            ),
            uiState = UiState(loading = false, scanning = false)
        )
    }
}
