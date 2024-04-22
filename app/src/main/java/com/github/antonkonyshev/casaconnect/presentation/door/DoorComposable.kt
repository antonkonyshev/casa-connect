package com.github.antonkonyshev.casaconnect.presentation.door

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.Device
import com.github.antonkonyshev.casaconnect.presentation.common.UiState
import com.github.antonkonyshev.casaconnect.presentation.common.collectAsEffect
import com.github.antonkonyshev.casaconnect.presentation.common.getActivity
import com.github.antonkonyshev.casaconnect.presentation.navigation.AppNavRouting
import com.github.antonkonyshev.casaconnect.ui.theme.CasaConnectTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun DoorScreen(
    viewModel: DoorViewModel = viewModel()
) {
    val device by viewModel.device.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.e("camera", "Screen VM: ${viewModel.toString()}")

    DoorScreenContent(
        device, viewModel.frame, uiState, viewModel.isPlaying,
        viewModel::togglePlaying
    )

    LocalContext.current.getActivity()?.eventBus?.collectAsEffect {
        if (it.id == "LoadCameraFrame")
            viewModel.loadCameraFrame()
    }
}

@Composable
fun DoorScreenContent(
    device: Device?, frame: StateFlow<Bitmap>,
    uiState: UiState, isPlaying: StateFlow<Boolean> = MutableStateFlow(true).asStateFlow(),
    togglePlaying: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Crossfade(targetState = uiState.loading, label = "Door Screen Loading") { loading ->
            if (!loading) {
                if (device != null) {

                    CameraFrame(
                        frame.collectAsStateWithLifecycle().value,
                        isPlaying.collectAsStateWithLifecycle().value,
                        togglePlaying
                    )

                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stringResource(id = R.string.door_sensors_not_found),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )

                        val currentActivity = LocalContext.current.getActivity()
                        Button(
                            onClick = {
                                currentActivity?.emitUiEvent(
                                    "NavigateTo",
                                    "${AppNavRouting.route_devices}?discover=true"
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.discover_devices),
                            )
                        }
                    }
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

@Composable
private fun CameraFrame(
    frame: Bitmap,
    isPlaying: Boolean,
    togglePlaying: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .aspectRatio(4f / 3f)
            .fillMaxSize()
    ) {
        Image(
            bitmap = frame.asImageBitmap(),
            contentDescription = stringResource(id = R.string.camera_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            IconButton(
                onClick = togglePlaying,
                modifier = Modifier
                    .padding(8.dp)
                    .size(70.dp)
                    .border(BorderStroke(2.dp, Color.White), CircleShape)
            ) {
                AnimatedContent(targetState = isPlaying, label = "Play button", transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }) {
                    Icon(
                        imageVector = if (it) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenVerticalPreview() {
    CasaConnectTheme {
        DoorScreenContent(
            Device("door-1", "door", "Door", listOf("picture"), available = true),
            MutableStateFlow(Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565)).asStateFlow(),
            uiState = UiState(scanning = false, loading = false)
        )
    }
}

@Preview(showBackground = true, heightDp = 180, widthDp = 300)
@Composable
fun DoorScreenHorizontalPreview() {
    CasaConnectTheme {
        DoorScreenContent(
            Device("door-1", "door", "Door", listOf("picture"), available = true),
            MutableStateFlow(Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565)).asStateFlow(),
            uiState = UiState(scanning = false, loading = false)
        )
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenWithoutDevicePreview() {
    CasaConnectTheme {
        DoorScreenContent(
            null,
            MutableStateFlow(Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565)).asStateFlow(),
            UiState(scanning = false, loading = false)
        )
    }
}

@Preview(showBackground = true, heightDp = 300, widthDp = 300)
@Composable
fun DoorScreenLoadingPreview() {
    CasaConnectTheme {
        DoorScreenContent(
            null,
            MutableStateFlow(Bitmap.createBitmap(160, 120, Bitmap.Config.RGB_565)).asStateFlow(),
            UiState(scanning = true, loading = true)
        )
    }
}
