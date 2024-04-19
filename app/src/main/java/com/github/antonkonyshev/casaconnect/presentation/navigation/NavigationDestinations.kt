package com.github.antonkonyshev.casaconnect.presentation.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf
import com.github.antonkonyshev.casaconnect.presentation.common.NavigationType

object NavigationDestinations {
    const val METEO = "meteo"
    const val LIGHT = "light"
    const val DOOR = "door"
    const val DEVICES = "devices"
    const val SETTINGS = "settings"
}

val LocalWindowWidthSizeClass = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalNavigationType = compositionLocalOf { NavigationType.BOTTOM_NAVIGATION }
val LocalNavigationDestination = compositionLocalOf { NavigationDestinations.METEO }