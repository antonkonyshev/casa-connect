package name.antonkonyshev.home.presentation.device

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material.icons.outlined.WaterDrop
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import name.antonkonyshev.home.R
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.presentation.LocalizationUtils
import name.antonkonyshev.home.presentation.UiState
import name.antonkonyshev.home.presentation.devicepreference.DevicePreferenceFragment
import java.net.Inet4Address

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesScreen(
    devices: List<Device>,
    selectedDevice: Device?,
    uiState: UiState,
    windowSize: WindowWidthSizeClass,
    onDiscoverDevicesClicked: () -> Unit = {},
    onDeviceClicked: (Device) -> Unit = {},
    onDrawerClicked: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(start = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(visible = windowSize == WindowWidthSizeClass.Compact) {
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.navigation_label)
                    )
                }
            }
            Text(
                text = stringResource(R.string.devices),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDiscoverDevicesClicked) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }

        Row {
            val refreshingState = rememberPullRefreshState(
                uiState.loading, onDiscoverDevicesClicked
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
                                text = LocalizationUtils.localizeDefaultServiceName(
                                    device.name, LocalContext.current
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            )
                        },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Outlined.Thermostat,
                                    contentDescription = null,
                                    modifier = Modifier.size(size = 40.dp)
                                )
                            },
                            supportingContent = {
                                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                                    val iconsModifier = Modifier.padding(end = 18.dp)
                                    if ("pollution" in device.sensors) {
                                        Icon(
                                            imageVector = Icons.Outlined.Masks,
                                            contentDescription = null,
                                            modifier = iconsModifier,
                                        )
                                    }
                                    if ("pressure" in device.sensors) {
                                        Icon(
                                            imageVector = Icons.Outlined.TireRepair,
                                            contentDescription = null,
                                            modifier = iconsModifier,
                                        )
                                    }
                                    if ("altitude" in device.sensors) {
                                        Icon(
                                            imageVector = Icons.Outlined.Landscape,
                                            contentDescription = null,
                                            modifier = iconsModifier,
                                        )
                                    }
                                    if ("humidity" in device.sensors) {
                                        Icon(
                                            imageVector = Icons.Outlined.WaterDrop,
                                            contentDescription = null,
                                            modifier = iconsModifier,
                                        )
                                    }
                                }
                            },
                            trailingContent = {
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
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = if (device.id == selectedDevice?.id) Color.White
                                else Color.White.copy(alpha = 0.7F),
                            ),
                            modifier = Modifier
                                .padding(bottom = 18.dp)
                                .clickable { onDeviceClicked(device) })
                    }
                }
                PullRefreshIndicator(
                    uiState.loading, refreshingState, Modifier.align(Alignment.TopCenter)
                )
            }

            AnimatedVisibility(
                visible = selectedDevice is Device,
                modifier = if (windowSize == WindowWidthSizeClass.Compact) Modifier.fillMaxSize()
                else Modifier.weight(3f)
            ) {
                if (selectedDevice != null) {
                    AndroidView(factory = { context ->
                        FragmentContainerView(context).apply {
                            id = ViewCompat.generateViewId()
                            (context as AppCompatActivity).supportFragmentManager.commit {
                                setReorderingAllowed(true)
                                add(id, DevicePreferenceFragment.newInstance(selectedDevice))
                            }
                        }
                    })
                }
            }
        }
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
            "kitchen-1",
            "meteo",
            "Kitchen",
            listOf("temperature", "pressure", "pollution"),
            Inet4Address.getByName("192.168.0.101"),
            false
        )
    )
    DevicesScreen(
        devices = devices,
        selectedDevice = null,
        UiState(loading = false, scanning = false),
        windowSize = WindowWidthSizeClass.Compact
    )
}
