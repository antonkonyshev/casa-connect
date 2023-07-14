package name.antonkonyshev.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import name.antonkonyshev.home.utils.DevicePosture
import name.antonkonyshev.home.utils.HomeNavigationType
import name.antonkonyshev.home.utils.getBackgroundPainter

// TODO: Add dark theme with night wallpapers
@Composable
fun HomeApp(
    homeUIState: HomeUIState,
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture
) {
    val navigationType: HomeNavigationType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = HomeNavigationType.BOTTOM_NAVIGATION
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = HomeNavigationType.NAVIGATION_RAIL
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                HomeNavigationType.NAVIGATION_RAIL
            } else {
                HomeNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }
        else -> {
            navigationType = HomeNavigationType.BOTTOM_NAVIGATION
        }
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = MaterialTheme.colorScheme.secondary,
        darkIcons = false,
    )

    HomeNavigationWrapperUI(navigationType, homeUIState)
}

@Composable
private fun HomeNavigationWrapperUI(
    navigationType: HomeNavigationType,
    homeUIState: HomeUIState,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (navigationType == HomeNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerContent()
                }
            }
        ) {
            HomeAppContent(navigationType, homeUIState)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )

                }
            },
            drawerState = drawerState
        ) {
            HomeAppContent(
                navigationType, homeUIState,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

// TODO: Visibility animation for the background image
@Composable
fun HomeAppContent(
    navigationType: HomeNavigationType,
    homeUIState: HomeUIState,
    onDrawerClicked: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = getBackgroundPainter(backgroundResource = viewModel.meteoBackgroundResource),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3F,
                modifier = Modifier.fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = navigationType == HomeNavigationType.NAVIGATION_RAIL
            ) {
                HomeNavigationRail(onDrawerClicked = onDrawerClicked)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AnimatedVisibility(visible = viewModel.selectedDestination.value == HomeDestinations.METEO) {
                    MeteoContent(homeUIState, Modifier.weight(1f))
                }
                AnimatedVisibility(visible = viewModel.selectedDestination.value == HomeDestinations.DEVICES) {
                    DevicesContent()
                }

                AnimatedVisibility(visible = navigationType == HomeNavigationType.BOTTOM_NAVIGATION) {
                    HomeBottomNavigationBar()
                }
            }
        }
    }
}