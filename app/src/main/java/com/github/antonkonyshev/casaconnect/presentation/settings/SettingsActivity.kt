package com.github.antonkonyshev.casaconnect.presentation.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class SettingsActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SettingsViewModel by viewModels()

        setContent {
            val windowSize = calculateWindowSizeClass(activity = this).widthSizeClass
            CasaConnectTheme {
                NavigationWrapper(
                    windowSize,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.SETTINGS,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        SettingsScreen(
                            windowSize,
                            onDrawerClicked
                        )
                    }
                )
            }
        }
    }
}