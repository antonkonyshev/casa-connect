package com.github.antonkonyshev.casaconnect.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onLoading() {
        _uiState.update { it.copy(loading = true) }
    }

    fun onLoaded() {
        _uiState.update { it.copy(loading = false) }
    }
}

data class UiState(
    val loading: Boolean = false,
    val scanning: Boolean = false,
)
