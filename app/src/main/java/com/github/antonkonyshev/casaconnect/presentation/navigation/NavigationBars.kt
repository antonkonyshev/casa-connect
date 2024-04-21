package com.github.antonkonyshev.casaconnect.presentation.navigation

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.common.BackgroundImage
import com.github.antonkonyshev.casaconnect.presentation.device.DevicesViewModel
import com.github.antonkonyshev.casaconnect.presentation.meteo.MeteoViewModel

@Composable
fun AppTopBarActions(currentScreen: AppNavRouting?) {
    if (currentScreen?.route == AppNavRouting.route_meteo) {
        val viewModel: MeteoViewModel = viewModel()
        IconButton(onClick = viewModel::observeMeasurement) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = stringResource(id = R.string.refresh)
            )
        }
    } else if (currentScreen?.route == AppNavRouting.route_devices) {
        val viewModel: DevicesViewModel = viewModel()
        IconButton(onClick = viewModel::discoverDevices) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = stringResource(id = R.string.refresh)
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(navController: NavHostController, onDrawerClicked: () -> Unit) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentScreen = AppNavRouting.screens.firstOrNull { screen ->
        currentDestination?.hierarchy?.any { it.route?.contains(screen.route) ?: false } == true
    }
    TopAppBar(
        title = {
            Text(
                text = if (currentScreen != null) stringResource(id = currentScreen.label)
                else stringResource(id = R.string.app_name)
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
            AppTopBarActions(currentScreen)
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
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar(
        containerColor = Color.Transparent
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        AppNavRouting.screens.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route?.contains(screen.route) ?: false
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(id = screen.label)
                    )
                }
            )
        }
    }
}

@Composable
fun NavigationDrawerContent(navController: NavHostController, onDrawerClicked: () -> Unit) {
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
    ) {
        Column {
            BackgroundImage(1)
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

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            AppNavRouting.screens.forEach { screen ->
                NavigationDrawerItem(
                    selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(screen.route) ?: false
                    } == true,
                    label = {
                        Text(
                            text = stringResource(screen.label),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(id = screen.label)
                        )
                    },
                    onClick = {
                        onDrawerClicked()
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun AppNavigationRail(navController: NavHostController) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = Color.Transparent)
            .verticalScroll(ScrollState(0)),
        containerColor = Color.Transparent,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        AppNavRouting.screens.forEach { screen ->
            NavigationRailItem(
                selected = currentDestination?.hierarchy?.any {
                    it.route?.contains(screen.route) ?: false
                } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(id = screen.label)
                    )
                }
            )
        }
    }
}