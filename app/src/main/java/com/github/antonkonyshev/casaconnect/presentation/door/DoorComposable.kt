package com.github.antonkonyshev.casaconnect.presentation.door

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.antonkonyshev.casaconnect.presentation.common.UiState

@Composable
fun DoorScreen(viewModel: DoorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DoorScreenContent(uiState)
}

@Composable
fun DoorScreenContent(uiState: UiState) {
    Column {
        Text(text = "Add door camera output")
    }
}

@Preview(showBackground = true)
@Composable
fun DoorScreenPreview() {
    DoorScreenContent(uiState = UiState(scanning = false, loading = false))
}