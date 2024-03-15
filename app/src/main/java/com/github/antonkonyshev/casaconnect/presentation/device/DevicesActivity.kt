package com.github.antonkonyshev.casaconnect.presentation.device

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.presentation.BaseActivity
import com.github.antonkonyshev.casaconnect.presentation.NavigationDestinations
import com.github.antonkonyshev.casaconnect.presentation.NavigationWrapper
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

class DevicesActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: DevicesViewModel by viewModels()

        setContent {
            val windowSize = calculateWindowSizeClass(activity = this).widthSizeClass
            CasaConnectTheme {
                NavigationWrapper(
                    windowSize,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.DEVICES,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        DevicesScreen(
                            viewModel.getDevicesByServiceUseCase.getAllDevicesFlow()
                                .collectAsState(initial = emptyList()).value,
                            selectedDevice = viewModel.selectedDevice.collectAsState().value,
                            uiState = viewModel.uiState.collectAsState().value,
                            windowSize = windowSize,
                            onDiscoverDevicesClicked = viewModel::discoverDevices,
                            onDeviceClicked = { device: Device ->
                                viewModel._selectedDevice.value = device
                            },
                            onDrawerClicked = onDrawerClicked
                        )
                    }
                )
            }
        }
    }

    fun deselectDevice() {
        val viewModel: DevicesViewModel by viewModels()
        viewModel._selectedDevice.value = null
    }
}
