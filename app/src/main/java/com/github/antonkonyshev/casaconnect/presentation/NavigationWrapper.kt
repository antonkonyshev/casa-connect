package com.github.antonkonyshev.casaconnect.presentation

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.device.DevicesActivity
import com.github.antonkonyshev.casaconnect.presentation.meteo.MeteoActivity
import com.github.antonkonyshev.casaconnect.presentation.settings.SettingsActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

object NavigationDestinations {
    const val METEO = "meteo"
    const val WINDOW = "window"
    const val LIGHT = "light"
    const val DOOR = "door"
    const val DEVICES = "devices"
    const val SETTINGS = "settings"
}

val LocalWindowWidthSizeClass = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalNavigationType = compositionLocalOf { NavigationType.BOTTOM_NAVIGATION }
val LocalNavigationDestination = compositionLocalOf { NavigationDestinations.METEO }

@Composable
fun AppScreen(
    sectionScreenComposable: @Composable () -> Unit = {},
    onDrawerClicked: () -> Unit
) {
    val currentActivity = LocalContext.current.getActivity()
    Scaffold(
        topBar = {
            AppTopBar(onDrawerClicked)
        },
        bottomBar = {
            if (LocalNavigationType.current == NavigationType.BOTTOM_NAVIGATION)
                BottomNavigationBar()
        }
    ) { contentPaddings ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddings)
        ) {
            AnimatedVisibility(LocalNavigationType.current == NavigationType.NAVIGATION_RAIL) {
                AppNavigationRail()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Row(modifier = Modifier.weight(1f)) {
                    sectionScreenComposable()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onDrawerClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = LocalContext.current.getActivity()?.header
                    ?: stringResource(id = R.string.app_name)
            )
        },
        navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = stringResource(
                        id = R.string.navigation_label
                    )
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = stringResource(id = R.string.devices)
                )
            }
        })
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NavigationWrapper(
    sectionScreenComposable: @Composable () -> Unit = {},
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = Color.White,
        darkIcons = true,
    )

    val currentActivity = LocalContext.current.getActivity()
    val windowWidthSizeClass = currentActivity?.let {
        calculateWindowSizeClass(activity = it).widthSizeClass
    } ?: WindowWidthSizeClass.Compact

    val foldingDevicePosture =
        currentActivity?.devicePostureFlow()?.collectAsStateWithLifecycle()?.value

    val navigationType: NavigationType
    when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM_NAVIGATION
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NAVIGATION_RAIL
        }

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
    val drawerScope = rememberCoroutineScope()
    val navigationDestination = currentActivity?.navigationDestination ?: ""

    CompositionLocalProvider(
        LocalWindowWidthSizeClass provides windowWidthSizeClass,
        LocalNavigationDestination provides navigationDestination,
        LocalNavigationType provides navigationType
    ) {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            if (currentActivity?.viewModel?.backgroundResource != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = getBackgroundPainter(
                            backgroundResource = currentActivity.viewModel.backgroundResource
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = 0.3F,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (LocalNavigationType.current == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet {
                            NavigationDrawerContent(onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                        }
                    }
                ) {
                    AppScreen(
                        sectionScreenComposable,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            } else {
                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            NavigationDrawerContent(
                                currentActivity?.viewModel?.navigationBackgroundResource,
                                onDrawerClicked = { drawerScope.launch { drawerState.open() } }
                            )
                        }
                    },
                    drawerState = drawerState
                ) {
                    AppScreen(
                        sectionScreenComposable,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    val context = LocalContext.current
    val navigationDestination = LocalNavigationDestination.current
    NavigationBar(
        modifier = Modifier
            .background(color = Color.Transparent)
            .height(80.dp)
            .fillMaxWidth(),
        containerColor = Color.Transparent,
    ) {
        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.METEO,
            onClick = {
                if (navigationDestination != NavigationDestinations.METEO) {
                    startActivity(
                        context,
                        Intent(context, MeteoActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = stringResource(R.string.meteo_label)
                )
            }
        )

        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.WINDOW,
            onClick = {
                startActivity(context, Intent(context, SettingsActivity::class.java), null)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = stringResource(R.string.window_label)
                )
            }
        )

        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.LIGHT,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Light,
                    contentDescription = stringResource(R.string.light_label)
                )
            }
        )

        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.DOOR,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = stringResource(R.string.door_label)
                )
            }
        )

        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.DEVICES,
            onClick = {
                if (navigationDestination != NavigationDestinations.DEVICES) {
                    startActivity(
                        context,
                        Intent(context, DevicesActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = stringResource(R.string.devices_label)
                )
            }
        )

        NavigationBarItem(
            selected = navigationDestination == NavigationDestinations.SETTINGS,
            onClick = {
                if (navigationDestination != NavigationDestinations.SETTINGS) {
                    startActivity(
                        context,
                        Intent(context, SettingsActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        )
    }
}

@Composable
fun NavigationDrawerContent(
    navigationBackgroundResource: Int? = null,
    onDrawerClicked: () -> Unit
) {
    val context = LocalContext.current
    val navigationDestination = LocalNavigationDestination.current
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        if (navigationBackgroundResource != null) {
            Column {
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
            Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .verticalScroll(ScrollState(0))
        ) {
            Row(
                modifier = Modifier
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
                        imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = stringResource(R.string.navigation_label)
                    )
                }
            }

            NavigationDrawerItem(
                selected = LocalNavigationDestination.current == NavigationDestinations.METEO,
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
                onClick = {
                    if (navigationDestination != NavigationDestinations.METEO) {
                        startActivity(
                            context,
                            Intent(context, MeteoActivity::class.java), null
                        )
                    }
                },
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
                onClick = {
                    if (navigationDestination != NavigationDestinations.DEVICES) {
                        startActivity(
                            context,
                            Intent(context, DevicesActivity::class.java), null
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            NavigationDrawerItem(
                selected = navigationDestination == NavigationDestinations.SETTINGS,
                label = {
                    Text(
                        text = stringResource(R.string.settings),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings)
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = {
                    if (navigationDestination != NavigationDestinations.SETTINGS) {
                        startActivity(
                            context,
                            Intent(context, SettingsActivity::class.java), null
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    val context = LocalContext.current
    val navigationDestination = context.getActivity()?.navigationDestination
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
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.navigation_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.METEO,
            onClick = {
                if (navigationDestination != NavigationDestinations.METEO) {
                    startActivity(
                        context,
                        Intent(context, MeteoActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = stringResource(R.string.meteo_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.WINDOW,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Air,
                    contentDescription = stringResource(R.string.window_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.LIGHT,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Light,
                    contentDescription = stringResource(R.string.light_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.DOOR,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = stringResource(R.string.door_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.DEVICES,
            onClick = {
                if (navigationDestination != NavigationDestinations.DEVICES) {
                    startActivity(
                        context,
                        Intent(context, DevicesActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = stringResource(R.string.devices_label)
                )
            },
        )

        NavigationRailItem(
            selected = navigationDestination == NavigationDestinations.SETTINGS,
            onClick = {
                if (navigationDestination != NavigationDestinations.SETTINGS) {
                    startActivity(
                        context,
                        Intent(context, SettingsActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            },
        )
    }
}
