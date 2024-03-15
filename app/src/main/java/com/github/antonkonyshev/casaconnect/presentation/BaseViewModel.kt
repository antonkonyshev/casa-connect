package com.github.antonkonyshev.casaconnect.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.github.antonkonyshev.casaconnect.R

open class BaseViewModel : ViewModel() {
    protected val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val backgroundResources = listOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    ).shuffled()
    val backgroundResource = backgroundResources[0]
    val navigationBackgroundResource = backgroundResources.drop(1)[0]

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
