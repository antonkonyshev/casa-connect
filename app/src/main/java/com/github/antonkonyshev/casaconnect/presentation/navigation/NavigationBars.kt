package com.github.antonkonyshev.casaconnect.presentation.navigation

import android.content.Intent
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
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.device.DevicesActivity
import com.github.antonkonyshev.casaconnect.presentation.getActivity
import com.github.antonkonyshev.casaconnect.presentation.getBackgroundPainter
import com.github.antonkonyshev.casaconnect.presentation.meteo.MeteoActivity
import com.github.antonkonyshev.casaconnect.presentation.settings.SettingsActivity

@Composable
fun AppTopBarActions() {
    val currentActivity = LocalContext.current.getActivity()
    if (LocalNavigationDestination.current == NavigationDestinations.METEO && currentActivity is MeteoActivity) {
        IconButton(onClick = { currentActivity.onMeasurementsRefresh() }) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = stringResource(id = R.string.refresh)
            )
        }
    } else if (LocalNavigationDestination.current == NavigationDestinations.DEVICES && currentActivity is DevicesActivity) {
        IconButton(onClick = { currentActivity.onDiscoverDevices() }) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = stringResource(id = R.string.refresh)
            )

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
        }, navigationIcon = {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = stringResource(
                        id = R.string.navigation_label
                    )
                )
            }
        }, actions = {
            AppTopBarActions()
        }, colors = TopAppBarColors(
            Color.Transparent,
            Color.Transparent,
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        ), modifier = Modifier.padding(start = 12.dp)
    )
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
        NavigationBarItem(selected = navigationDestination == NavigationDestinations.METEO,
            onClick = {
                if (navigationDestination != NavigationDestinations.METEO) {
                    ContextCompat.startActivity(
                        context, Intent(context, MeteoActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = stringResource(R.string.meteo_label)
                )
            })

        NavigationBarItem(selected = navigationDestination == NavigationDestinations.LIGHT,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Light,
                    contentDescription = stringResource(R.string.light_label)
                )
            })

        NavigationBarItem(selected = navigationDestination == NavigationDestinations.DOOR,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = stringResource(R.string.door_label)
                )
            })

        NavigationBarItem(selected = navigationDestination == NavigationDestinations.DEVICES,
            onClick = {
                if (navigationDestination != NavigationDestinations.DEVICES) {
                    ContextCompat.startActivity(
                        context, Intent(context, DevicesActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = stringResource(R.string.devices_label)
                )
            })

        NavigationBarItem(selected = navigationDestination == NavigationDestinations.SETTINGS,
            onClick = {
                if (navigationDestination != NavigationDestinations.SETTINGS) {
                    ContextCompat.startActivity(
                        context, Intent(context, SettingsActivity::class.java), null
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            })
    }
}

@Composable
fun NavigationDrawerContent(
    navigationBackgroundResource: Int? = null, onDrawerClicked: () -> Unit
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
                        ContextCompat.startActivity(
                            context, Intent(context, MeteoActivity::class.java), null
                        )
                    }
                },
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
                        ContextCompat.startActivity(
                            context, Intent(context, DevicesActivity::class.java), null
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
                        ContextCompat.startActivity(
                            context, Intent(context, SettingsActivity::class.java), null
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun AppNavigationRail() {
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
            selected = navigationDestination == NavigationDestinations.METEO,
            onClick = {
                if (navigationDestination != NavigationDestinations.METEO) {
                    ContextCompat.startActivity(
                        context, Intent(context, MeteoActivity::class.java), null
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
                    ContextCompat.startActivity(
                        context, Intent(context, DevicesActivity::class.java), null
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
                    ContextCompat.startActivity(
                        context, Intent(context, SettingsActivity::class.java), null
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