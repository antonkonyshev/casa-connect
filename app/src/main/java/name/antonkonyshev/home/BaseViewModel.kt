package name.antonkonyshev.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import name.antonkonyshev.home.devices.Device

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    protected val backgroundResources = listOf(
        R.raw.river,
        R.raw.lake,
        R.raw.mountains,
        R.raw.praire,
    ).shuffled()
    val backgroundResource = backgroundResources[0]
    val navigationBackgroundResource = backgroundResources.drop(1)[0]
}