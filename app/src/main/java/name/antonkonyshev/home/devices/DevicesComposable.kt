package name.antonkonyshev.home.devices

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.TireRepair
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import name.antonkonyshev.home.R
import name.antonkonyshev.home.getActivity
import name.antonkonyshev.home.meteo.MeteoActivity
import name.antonkonyshev.home.utils.getLocalServiceName

@Composable
fun DevicesScreen(
    loading: Boolean,
    devices: List<Device>
) {
    val context = LocalContext.current;
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .padding(start = 25.dp, end = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.devices),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
            )
            AnimatedVisibility(!loading) {
                Button(
                    onClick = { context.getActivity()?.discoveryService?.discoverDevices() },
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

        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier
        ) {
            items(devices) { device ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = getLocalServiceName(device.name, LocalContext.current),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top=10.dp, bottom = 10.dp)
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
                        Row (modifier = Modifier.padding(bottom=10.dp)) {
                            val iconsModifier = Modifier.padding(end=18.dp)
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
                        if (device.available == true) {
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
                        containerColor = Color.White.copy(alpha=0.7F),
                    ),
                    modifier = Modifier
                        .padding(bottom = 18.dp)
                        .clickable {
                            startActivity(context,
                                Intent(context, MeteoActivity::class.java)
                                    .apply { putExtra("deviceId", device.id) },
                                null)
                        }
                )
            }
        }
    }
}
