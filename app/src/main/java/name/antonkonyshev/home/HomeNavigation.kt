package name.antonkonyshev.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import name.antonkonyshev.home.utils.getBackgroundPainter

object HomeDestinations {
    const val METEO = "Meteo"
    const val WINDOW = "Window"
    const val LIGHT = "Light"
    const val DOOR = "Door"
    const val DEVICES = "Devices"
}

@Composable
fun HomeBottomNavigationBar(
    viewModel: HomeViewModel = viewModel(),
) {
    NavigationBar(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        NavigationBarItem(
            selected = viewModel.selectedDestination.value == HomeDestinations.METEO,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Thermostat,
                    contentDescription = stringResource(R.string.meteo_label))
            }
        )
        NavigationBarItem(
            selected = viewModel.selectedDestination.value == HomeDestinations.WINDOW,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Air,
                    contentDescription = stringResource(R.string.window_label))
            }
        )
        NavigationBarItem(
            selected = viewModel.selectedDestination.value == HomeDestinations.LIGHT,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Light,
                    contentDescription = stringResource(R.string.light_label))
            }
        )
        NavigationBarItem(
            selected = viewModel.selectedDestination.value == HomeDestinations.DOOR,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.MeetingRoom,
                    contentDescription = stringResource(R.string.door_label))
            }
        )
        NavigationBarItem(
            selected = viewModel.selectedDestination.value == HomeDestinations.DEVICES,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Sensors,
                    contentDescription = stringResource(R.string.devices_label))
            }
        )
    }
}

@Composable
fun NavigationDrawerContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onDrawerClicked: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = modifier) {
            Image(
                painter = getBackgroundPainter(viewModel.navigationDrawerBackgroundResource),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3F,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(
            modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .verticalScroll(ScrollState(0))
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        imageVector = Icons.Default.MenuOpen,
                        contentDescription = stringResource(R.string.navigation_label)
                    )
                }
            }

            NavigationDrawerItem(
                selected = viewModel.selectedDestination.value == HomeDestinations.METEO,
                label = {
                    Text(
                        text = stringResource(R.string.meteo_label),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = stringResource(R.string.meteo_label)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            NavigationDrawerItem(
                selected = viewModel.selectedDestination.value == HomeDestinations.WINDOW,
                label = {
                    Text(
                        text = stringResource(R.string.window_label),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Air,
                        contentDescription = stringResource(R.string.window_label)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            NavigationDrawerItem(
                selected = viewModel.selectedDestination.value == HomeDestinations.LIGHT,
                label = {
                    Text(
                        text = stringResource(R.string.light_label),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Light,
                        contentDescription = stringResource(R.string.light_label)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            NavigationDrawerItem(
                selected = viewModel.selectedDestination.value == HomeDestinations.DOOR,
                label = {
                    Text(
                        text = stringResource(R.string.door_label),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = stringResource(R.string.door_label)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            NavigationDrawerItem(
                selected = viewModel.selectedDestination.value == HomeDestinations.DEVICES,
                label = {
                    Text(
                        text = stringResource(R.string.devices_label),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = stringResource(R.string.devices_label)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
@Preview
fun HomeNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = Color.Transparent)
            .verticalScroll(ScrollState(0)),
        containerColor = Color.Transparent,
    ) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.navigation_label) ) },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Thermostat,
                contentDescription = stringResource(R.string.meteo_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Air,
                contentDescription = stringResource(R.string.window_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Light,
                contentDescription = stringResource(R.string.light_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.MeetingRoom,
                contentDescription = stringResource(R.string.door_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Sensors,
                contentDescription = stringResource(R.string.devices_label)) },
        )
    }
}
