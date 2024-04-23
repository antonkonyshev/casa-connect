package com.github.antonkonyshev.casaconnect.presentation.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseActivity : AppCompatActivity() {
    private val _eventBus = MutableSharedFlow<UiEvent>()
    val eventBus = _eventBus.asSharedFlow()

    fun emitUiEvent(id: String, extras: String = "") {
        lifecycleScope.launch {
            _eventBus.emit(UiEvent(id, extras))
        }
    }

    fun emitUiEvent(uiEvent: UiEvent) {
        lifecycleScope.launch {
            _eventBus.emit(uiEvent)
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

data class UiEvent(val id: String, val extra: String = "")

fun Context.getActivity(): BaseActivity? = when (this) {
    is BaseActivity -> this
    is ContextWrapper -> baseContext.getActivity() as BaseActivity
    else -> null
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(Unit) {
        onEach(block).flowOn(context).launchIn(this)
    }
}