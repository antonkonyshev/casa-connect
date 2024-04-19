package com.github.antonkonyshev.casaconnect.presentation.common

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

abstract class BaseActivity : AppCompatActivity() {

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
    is ContextWrapper -> baseContext.getActivity() as BaseActivity
    else -> null
}