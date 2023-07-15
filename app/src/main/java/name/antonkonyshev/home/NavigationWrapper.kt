package name.antonkonyshev.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import name.antonkonyshev.home.devices.Device
import name.antonkonyshev.home.devices.DevicesScreen
import name.antonkonyshev.home.meteo.MeteoScreen
import name.antonkonyshev.home.utils.DevicePosture
import name.antonkonyshev.home.utils.NavigationType
import name.antonkonyshev.home.utils.getBackgroundPainter

object NavigationDestinations {
    const val METEO = "Meteo"
    const val WINDOW = "Window"
    const val LIGHT = "Light"
    const val DOOR = "Door"
    const val DEVICES = "Devices"
}

// TODO: Visibility animation for the background image
@Composable
fun AppScreen(
    navigationType: NavigationType,
    backgroundResource: Int,
    navigationDestination: String,
    loading: Boolean,
    devices: List<Device>,
    onDrawerClicked: () -> Unit = {},
) {
    Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = getBackgroundPainter(backgroundResource = backgroundResource),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3F,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(navigationType == NavigationType.NAVIGATION_RAIL) {
                AppNavigationRail(onDrawerClicked = onDrawerClicked)
            }

            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                AnimatedVisibility(navigationDestination == NavigationDestinations.METEO) {
                    MeteoScreen(loading, devices)
                }
                AnimatedVisibility(navigationDestination == NavigationDestinations.DEVICES, modifier = Modifier.weight(1f)) {
                    DevicesScreen(loading, devices)
                }

                AnimatedVisibility(navigationType == NavigationType.BOTTOM_NAVIGATION) {
                    BottomNavigationBar(navigationDestination)
                }
            }
        }
    }
}

@Composable
fun NavigationWrapper(
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture,
    navigationDestination: String,
    navigationBackgroundResource: Int,
    backgroundResource: Int,
    loading: Boolean,
    devices: List<Device>,
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.secondary,
        darkIcons = false,
    )

    val navigationType: NavigationType
    when (windowSize) {
        WindowWidthSizeClass.Compact -> { navigationType = NavigationType.BOTTOM_NAVIGATION }
        WindowWidthSizeClass.Medium -> { navigationType = NavigationType.NAVIGATION_RAIL }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.NAVIGATION_RAIL
            } else {
                NavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerContent(navigationDestination)
                }
            }
        ) {
            AppScreen(navigationType, backgroundResource, navigationDestination, loading, devices)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        navigationDestination,
                        navigationBackgroundResource,
                        onDrawerClicked = { scope.launch { drawerState.close() } }
                    )

                }
            },
            drawerState = drawerState
        ) {
            AppScreen(
                navigationType, backgroundResource, navigationDestination, loading, devices,
                onDrawerClicked = { scope.launch { drawerState.open() } }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    navigationDestination: String,
) {
    NavigationBar(
        modifier = Modifier
            .background(color = Color.Transparent)
            .height(80.dp)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.METEO,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Thermostat,
                    contentDescription = stringResource(R.string.meteo_label))
            }
        )
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.WINDOW,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Air,
                    contentDescription = stringResource(R.string.window_label))
            }
        )
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.LIGHT,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.Light,
                    contentDescription = stringResource(R.string.light_label))
            }
        )
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.DOOR,
            onClick = {},
            icon = {
                Icon(imageVector = Icons.Default.MeetingRoom,
                    contentDescription = stringResource(R.string.door_label))
            }
        )
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.DEVICES,
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
    navigationDestination: String,
    navigationBackgroundResource: Int? = null,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = modifier) {
            if (navigationBackgroundResource != null) {
                Image(
                    painter = getBackgroundPainter(navigationBackgroundResource),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.3F,
                    modifier = Modifier.fillMaxSize()
                )
            }
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
                selected = navigationDestination == NavigationDestinations.METEO,
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
                selected = navigationDestination == NavigationDestinations.WINDOW,
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
                selected = navigationDestination == NavigationDestinations.LIGHT,
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
                selected = navigationDestination == NavigationDestinations.DOOR,
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
                selected = navigationDestination == NavigationDestinations.DEVICES,
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
fun AppNavigationRail(
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
            onClick = {},
            icon = { Icon(imageVector = Icons.Default.Thermostat,
                contentDescription = stringResource(R.string.meteo_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.Default.Air,
               contentDescription = stringResource(R.string.window_label)) },
        )
        NavigationRailItem(
            selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.Default.Light,
                contentDescription = stringResource(R.string.light_label)) },
        )
        NavigationRailItem(
            selected = true,
           onClick = {},
            icon = { Icon(imageVector = Icons.Default.MeetingRoom,
                contentDescription = stringResource(R.string.door_label)) },
        )
        NavigationRailItem(
           selected = true,
            onClick = {},
            icon = { Icon(imageVector = Icons.Default.Sensors,
                contentDescription = stringResource(R.string.devices_label)) },
        )
    }
}