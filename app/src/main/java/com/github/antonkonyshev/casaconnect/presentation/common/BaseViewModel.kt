package com.github.antonkonyshev.casaconnect.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onLoading(scanning: Boolean = false) {
        _uiState.update { it.copy(loading = true, scanning = scanning) }
    }

    fun onLoaded(scanning: Boolean = false) {
        _uiState.update { it.copy(loading = false, scanning = scanning) }
    }
}

data class UiState(
    val loading: Boolean = false,
    val scanning: Boolean = false,
)
