package com.github.antonkonyshev.casaconnect.presentation.device

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.presentation.common.UiState
import com.github.antonkonyshev.casaconnect.presentation.common.collectAsEffect
import com.github.antonkonyshev.casaconnect.presentation.common.getActivity
import com.github.antonkonyshev.casaconnect.presentation.devicepreference.DevicePreferenceScreen
import com.github.antonkonyshev.casaconnect.presentation.navigation.AppNavRouting
import com.github.antonkonyshev.casaconnect.presentation.navigation.LocalWindowWidthSizeClass
import java.net.Inet4Address

@Composable
fun DevicesScreen(viewModel: DevicesViewModel = viewModel(), deviceId: String = "") {
    if (deviceId.isNotBlank()) {
        when (deviceId) {
            "discover" -> {
                var discovered by rememberSaveable { mutableStateOf(false) }
                LaunchedEffect("discovering") {
                    if (!discovered) {
                        viewModel.discoverDevices()
                        discovered = true
                    }
                }
            }

            else -> {
                try {
                    viewModel.editDeviceById(deviceId)
                } catch(_: Exception) {}
            }
        }
    }

    val devices by viewModel.getDevicesByAttributeUseCase.getAllDevicesFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedDevice by viewModel.selectedDevice.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DevicesScreenContent(
        devices, selectedDevice, uiState, viewModel::editDevice, viewModel::discoverDevices,
        viewModel::cleanEditableDevice
    )

    LocalContext.current.getActivity()?.eventBus?.collectAsEffect {
        if (it.id == "DiscoverDevices")
            viewModel.discoverDevices()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesScreenContent(
    devices: List<Device>,
    selectedDevice: Device?,
    uiState: UiState,
    selectDevice: (Device) -> Unit = {},
    discoverDevices: () -> Unit = {},
    deselectDevice: () -> Unit = {}
) {
    Row {
        val refreshingState = rememberPullRefreshState(
            uiState.loading, discoverDevices
        )

        Box(
            modifier = Modifier
                .weight(2f)
                .pullRefresh(refreshingState)
        ) {

            LazyColumn(
                modifier = Modifier
            ) {
                items(devices) { device ->

                    ListItem(headlineContent = {
                        Text(
                            text = device.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        )
                    },
                        leadingContent = {
                            Icon(
                                imageVector = device.type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(size = 40.dp)
                            )
                        },
                        supportingContent = {
                            Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                val iconsModifier = Modifier.padding(end = 18.dp)
                                device.sensorTypesExcludingMain.sortedBy { it.ordering }.forEach {
                                    Icon(
                                        imageVector = it.icon,
                                        contentDescription = it.sensor,
                                        modifier = iconsModifier
                                    )
                                }
                            }
                        },
                        trailingContent = {
                            DeviceAvailabilityIcon(device)
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = if (device.id == selectedDevice?.id) Color.White
                            else Color.White.copy(alpha = 0.7F),
                        ),
                        modifier = Modifier
                            .padding(bottom = 18.dp)
                            .clickable { selectDevice(device) })
                }
            }
            PullRefreshIndicator(
                uiState.loading, refreshingState, Modifier.align(Alignment.TopCenter)
            )
        }

        AnimatedVisibility(
            visible = selectedDevice is Device,
            modifier = if (LocalWindowWidthSizeClass.current == WindowWidthSizeClass.Compact) {
                Modifier.fillMaxSize()
            } else {
                Modifier.weight(3f)
            }
        ) {
            if (selectedDevice != null) {
                DevicePreferenceScreen(selectedDevice, deselectDevice)
            }
        }
    }
}

@Composable
fun DeviceEditIcon(device: Device) {
    val currentActivity = LocalContext.current.getActivity()
    IconButton(
        onClick = {
            currentActivity?.emitUiEvent(
                "NavigateTo", "${AppNavRouting.route_devices}?deviceId=${device.id}"
            )
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = stringResource(id = R.string.device_preferences),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DeviceAvailabilityIcon(
    device: Device,
    modifier: Modifier = Modifier.padding(12.dp, 10.dp, 12.dp, 10.dp)
) {
    if (device.available) {
        Icon(
            imageVector = Icons.Default.Sensors,
            contentDescription = "Online",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
        )
    } else {
        Icon(
            imageVector = Icons.Default.SensorsOff,
            contentDescription = "Offline",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDevices() {
    val devices = listOf(
        Device(
            "room-1",
            "meteo",
            "Room",
            listOf("temperature", "pressure", "pollution", "altitude"),
            Inet4Address.getByName("192.168.0.101"),
            true
        ), Device(
            "door-1",
            "door",
            "Entrance",
            listOf("photo", "presence"),
            Inet4Address.getByName("192.168.0.102"),
            false
        )
    )
    DevicesScreenContent(devices, null, UiState(loading = false, scanning = false))
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun PreviewSelectedDevice() {
    val devices = listOf(
        Device(
            "room-1",
            "meteo",
            "Room",
            listOf("temperature", "pressure", "pollution", "altitude"),
            Inet4Address.getByName("192.168.0.101"),
            true
        ), Device(
            "door-1",
            "door",
            "Entrance",
            listOf("photo", "presence"),
            Inet4Address.getByName("192.168.0.102"),
            false
        )
    )
    DevicesScreenContent(devices, null, UiState(loading = false, scanning = false))
}
