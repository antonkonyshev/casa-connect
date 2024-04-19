package com.github.antonkonyshev.casaconnect.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.antonkonyshev.casaconnect.presentation.DevicePosture
import com.github.antonkonyshev.casaconnect.presentation.NavigationType
import com.github.antonkonyshev.casaconnect.presentation.getActivity
import com.github.antonkonyshev.casaconnect.presentation.getBackgroundPainter
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun AppScreen(
    sectionScreenComposable: @Composable () -> Unit = {}, onDrawerClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(onDrawerClicked)
        },
        bottomBar = {
            if (LocalNavigationType.current == NavigationType.BOTTOM_NAVIGATION) BottomNavigationBar()
        },
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
        Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
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
                PermanentNavigationDrawer(drawerContent = {
                    PermanentDrawerSheet {
                        NavigationDrawerContent(onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                    }
                }) {
                    AppScreen(sectionScreenComposable,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            } else {
                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            NavigationDrawerContent(currentActivity?.viewModel?.navigationBackgroundResource,
                                onDrawerClicked = { drawerScope.launch { drawerState.close() } })
                        }
                    }, drawerState = drawerState
                ) {
                    AppScreen(sectionScreenComposable,
                        onDrawerClicked = { drawerScope.launch { drawerState.open() } })
                }
            }
        }
    }
}

