package com.github.antonkonyshev.casaconnect.presentation.meteo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.common.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.navigation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.navigation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class MeteoActivity : BaseActivity() {
    override val viewModel: MeteoViewModel by viewModels()
    override var header: String = ""
    override val navigationDestination: String = NavigationDestinations.METEO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        header = resources.getString(R.string.meteostation)

        setContent {
            CasaConnectTheme {
                NavigationWrapper()
            }
        }
    }
}