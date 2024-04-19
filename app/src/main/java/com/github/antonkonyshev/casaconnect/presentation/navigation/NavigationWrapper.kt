package com.github.antonkonyshev.casaconnect.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.common.BackgroundImage
import com.github.antonkonyshev.casaconnect.presentation.common.DevicePosture
import com.github.antonkonyshev.casaconnect.presentation.common.NavigationType
import com.github.antonkonyshev.casaconnect.presentation.common.getActivity
import com.github.antonkonyshev.casaconnect.presentation.device.DevicesScreen
import com.github.antonkonyshev.casaconnect.presentation.meteo.MeteoScreen
import com.github.antonkonyshev.casaconnect.presentation.settings.SettingsScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

val LocalWindowWidthSizeClass = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalNavigationType = compositionLocalOf { NavigationType.BOTTOM_NAVIGATION }

sealed class AppNavRouting(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector
) {
    companion object {
        val screens = listOf(
            Meteo, Devices, Settings
        )

        const val route_meteo = "meteo"
        const val route_devices = "devices"
        const val route_settings = "settings"
    }

    private object Meteo : AppNavRouting(
        route_meteo, R.string.meteostation, Icons.Default.Thermostat
    )

    private object Devices : AppNavRouting(
        route_devices, R.string.devices, Icons.Default.Sensors
    )

    private object Settings : AppNavRouting(
        route_settings, R.string.settings, Icons.Default.Settings
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController, startDestination = AppNavRouting.route_meteo,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable(AppNavRouting.route_meteo) {
            MeteoScreen()
        }
        composable(AppNavRouting.route_devices) {
            DevicesScreen()
        }
        composable(AppNavRouting.route_settings) {
            SettingsScreen()
        }
    }
}

@Composable
fun AppScreen(
    navController: NavHostController,
    onDrawerClicked: () -> Unit
) {
    // TODO: Use snackbar
    val snackbarScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            AppTopBar(navController, onDrawerClicked)
        },
        bottomBar = {
            if (LocalNavigationType.current == NavigationType.BOTTOM_NAVIGATION) BottomNavigationBar(
                navController
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { contentPaddings ->
        // TODO: Check nested layouts, exclude unnecessary
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddings)
        ) {
            AnimatedVisibility(LocalNavigationType.current == NavigationType.NAVIGATION_RAIL) {
                AppNavigationRail(navController)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Row(modifier = Modifier.weight(1f)) {
                    AppNavHost(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun NavigationWrapper() {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true,
        )
    }

    val navController = rememberNavController()
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

    CompositionLocalProvider(
        LocalWindowWidthSizeClass provides windowWidthSizeClass,
        LocalNavigationType provides navigationType
    ) {
        Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.fillMaxSize()) {
                BackgroundImage()
            }

            if (LocalNavigationType.current == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
                PermanentNavigationDrawer(drawerContent = {
                    PermanentDrawerSheet {
                        NavigationDrawerContent(navController,
                            onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                    }
                }) {
                    AppScreen(navController,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            } else {
                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            NavigationDrawerContent(navController,
                                onDrawerClicked = { drawerScope.launch { drawerState.close() } })
                        }
                    }, drawerState = drawerState
                ) {
                    AppScreen(navController,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            }
        }
    }
}