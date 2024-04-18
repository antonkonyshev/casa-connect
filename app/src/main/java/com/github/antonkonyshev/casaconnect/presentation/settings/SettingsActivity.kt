package com.github.antonkonyshev.casaconnect.presentation.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class SettingsActivity : BaseActivity() {

    override val viewModel: SettingsViewModel by viewModels()
    override var header: String = ""
    override val navigationDestination: String = NavigationDestinations.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        header = resources.getString(R.string.settings)

        setContent {
            CasaConnectTheme {
                NavigationWrapper {
                    SettingsScreen()
                }
            }
        }
    }
}