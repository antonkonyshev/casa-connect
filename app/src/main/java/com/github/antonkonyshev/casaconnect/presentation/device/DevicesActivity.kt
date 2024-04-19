package com.github.antonkonyshev.casaconnect.presentation.device

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.presentation.common.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.navigation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.navigation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class DevicesActivity : BaseActivity() {

    override val viewModel: DevicesViewModel by viewModels()
    override var header: String = ""
    override val navigationDestination: String = NavigationDestinations.DEVICES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        header = resources.getString(R.string.devices)

        val viewModel: DevicesViewModel by viewModels()

        setContent {
            CasaConnectTheme {
                NavigationWrapper()
            }
        }
    }

    fun onDiscoverDevices() {
        viewModel.discoverDevices()
    }

    fun deselectDevice() {
        val viewModel: DevicesViewModel by viewModels()
        viewModel._selectedDevice.value = null
    }
}
