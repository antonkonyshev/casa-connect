package name.antonkonyshev.home.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import name.antonkonyshev.home.R

open class BaseViewModel() : ViewModel() {
    protected val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    protected val backgroundResources = listOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    ).shuffled()
    val backgroundResource = backgroundResources[0]
    val navigationBackgroundResource = backgroundResources.drop(1)[0]
}

data class UiState(
    val loading: Boolean = false,
    val scanning: Boolean = false,
)
