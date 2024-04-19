package com.github.antonkonyshev.casaconnect.presentation.device

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
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
                NavigationWrapper {
                    DevicesScreen(
                        viewModel.getDevicesByServiceUseCase.getAllDevicesFlow()
                            .collectAsStateWithLifecycle(initialValue = emptyList()).value,
                        selectedDevice = viewModel.selectedDevice.collectAsStateWithLifecycle()
                            .value,
                        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                        onDiscoverDevicesClicked = ::onDiscoverDevices,
                        onDeviceClicked = { device: Device ->
                            viewModel._selectedDevice.value = device
                        }
                    )
                }
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
