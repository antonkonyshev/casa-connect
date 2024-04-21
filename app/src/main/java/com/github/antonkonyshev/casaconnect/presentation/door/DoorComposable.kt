package com.github.antonkonyshev.casaconnect.presentation.door

import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.presentation.common.UiState
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme

@Composable
fun DoorScreen(
    viewModel: DoorViewModel = viewModel(),
    navigateToDevicesDiscovering: () -> Unit = {}
) {
    val device by viewModel.device.collectAsStateWithLifecycle()
    val picture by viewModel.picture.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DoorScreenContent(device, picture, uiState, navigateToDevicesDiscovering)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DoorScreenContent(
    device: Device?, picture: Bitmap?, uiState: UiState,
    navigateToDevicesDiscovering: () -> Unit = {}
) {
    val refreshingState = rememberPullRefreshState(uiState.loading, {
    })

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .pullRefresh(refreshingState)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {

        Crossfade(targetState = uiState.loading, label = "Door Screen Loading") { loading ->

            if (!uiState.loading) {
                if (device != null) {
                    if (picture != null) {
                        Image(
                            bitmap = picture.asImageBitmap(),
                            contentDescription = stringResource(R.string.camera_picture),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // TODO: Add control buttons
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.door_sensors_not_found),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = navigateToDevicesDiscovering,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.discover_devices),
                            )
                        }
                    }
                    // TODO: Button to refresh devices list
                }
            } else {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenPreview() {
    CasaConnectTheme {
        DoorScreenContent(
            Device("door-1", "door", "Door", listOf("picture"), available = true),
            Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565),
            uiState = UiState(scanning = false, loading = false)
        )
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenWithoutDevicePreview() {
    CasaConnectTheme {
        DoorScreenContent(null, null, UiState(scanning = false, loading = false))
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenLoadingPreview() {
    CasaConnectTheme {
        DoorScreenContent(null, null, UiState(scanning = true, loading = true))
    }
}
