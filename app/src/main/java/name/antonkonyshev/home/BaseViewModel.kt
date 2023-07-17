package name.antonkonyshev.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val devicesStateUpdatePeriod = 60L  // seconds

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

    init {
        /*
        Timer().scheduleAtFixedRate(delay = 0L, period = devicesStateUpdatePeriod * 1000L) {
            getApplication<HomeApplication>().discoveryService.discoverDevices()
        }
        */
    }
}

data class UiState(
    val loading: Boolean = false,
    val scanning: Boolean = false,
)
