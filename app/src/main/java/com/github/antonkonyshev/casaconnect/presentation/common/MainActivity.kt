package com.github.antonkonyshev.casaconnect.presentation.common

import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.antonkonyshev.casaconnect.presentation.navigation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CasaConnectTheme {
                NavigationWrapper()
            }
        }
    }
}