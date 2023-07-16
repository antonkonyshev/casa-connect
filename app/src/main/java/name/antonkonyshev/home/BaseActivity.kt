package name.antonkonyshev.home

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import name.antonkonyshev.home.devices.DiscoveryService
import name.antonkonyshev.home.utils.DevicePosture
import name.antonkonyshev.home.utils.isBookPosture
import name.antonkonyshev.home.utils.isSeparating
import org.lsposed.hiddenapibypass.HiddenApiBypass

open class BaseActivity : ComponentActivity() {
    lateinit var discoveryService: DiscoveryService
    protected var discoveryServiceBound = false

    protected val connection = object : ServiceConnection {
        override fun onServiceConnected(compName: ComponentName?, binder: IBinder?) {
            discoveryService = (binder as DiscoveryService.DiscoveryBinder).getService()
            discoveryServiceBound = true
        }

        override fun onServiceDisconnected(compName: ComponentName?) {
            discoveryServiceBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        discoveryServiceBound = false
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        HiddenApiBypass.addHiddenApiExemptions("L")
        super.onCreate(savedInstanceState)
        Intent(this, DiscoveryService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun devicePostureFlow(): StateFlow<DevicePosture> {
        return WindowInfoTracker
            .getOrCreate(this)
            .windowLayoutInfo(this)
            .flowWithLifecycle(this.lifecycle)
            .map { layoutInfo ->
                val foldingFeature = layoutInfo
                    .displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
                when {
                    isBookPosture(foldingFeature) ->
                        DevicePosture.BookPosture(foldingFeature?.bounds)

                    isSeparating(foldingFeature) ->
                        DevicePosture.Separating(
                            foldingFeature?.bounds,
                            foldingFeature?.orientation
                        )

                    else -> DevicePosture.NormalPosture
                }
            }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.Eagerly,
                initialValue = DevicePosture.NormalPosture
            )
    }
}

fun Context.getActivity(): BaseActivity? = when (this) {
    is BaseActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}