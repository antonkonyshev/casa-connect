package name.antonkonyshev.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeApp(homeUIState: HomeUIState) {
    HomeNavigationWrapperUI(homeUIState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeNavigationWrapperUI(
    homeUIState: HomeUIState
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedDestination = HomeDestinations.METEO

    HomeAppContent(homeUIState)
}

@Composable
fun HomeAppContent(
    homeUIState: HomeUIState,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier
        .fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            MeteoContent(homeUIState, Modifier.weight(1f))
            HomeBottomNavigationBar()
        }
    }
}

@Preview
@Composable
fun HomeBottomNavigationBar() {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Thermostat, contentDescription = "Meteo")
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Air, contentDescription = "Window")
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Light, contentDescription = "Light")
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = "Door")
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Sensors, contentDescription = "Devices")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    selectedDestination: String,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
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
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.MenuOpen,
                    contentDescription = "Navigation"
                )
            }
        }

        NavigationDrawerItem(
            selected = selectedDestination == HomeDestinations.METEO,
            label = { Text(text = "Meteo", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Thermostat, contentDescription = "Meteo") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = {}
        )
        NavigationDrawerItem(
            selected = selectedDestination == HomeDestinations.WINDOW,
            label = { Text(text = "Window", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Air, contentDescription = "Window") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = {}
        )
        NavigationDrawerItem(
            selected = selectedDestination == HomeDestinations.LIGHT,
            label = { Text(text = "Light", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Light, contentDescription = "Light") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = {}
        )
        NavigationDrawerItem(
            selected = selectedDestination == HomeDestinations.DOOR,
            label = { Text(text = "Door", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = "Door") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = {}
        )
        NavigationDrawerItem(
            selected = selectedDestination == HomeDestinations.DEVICES,
            label = { Text(text = "Devices", modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Sensors, contentDescription = "Devices") },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = {}
        )
    }
}

@Composable
@Preview
fun HomeNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Menu, contentDescription = "Navigation") },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Thermostat, contentDescription = "Meteo") },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Air, contentDescription = "Window") },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Light, contentDescription = "Light") },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = "Door") },
        )
        NavigationRailItem(
            selected = true,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Sensors, contentDescription = "Devices") },
        )
    }
}