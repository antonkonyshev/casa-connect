package com.github.antonkonyshev.casaconnect.presentation.common

import android.graphics.Rect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.github.antonkonyshev.casaconnect.R

fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

sealed interface DevicePosture {
    data object NormalPosture : DevicePosture

    data class BookPosture(
        val hingePosition: Rect?
    ) : DevicePosture

    data class Separating(
        val hingePosition: Rect?,
        var orientation: FoldingFeature.Orientation?
    ) : DevicePosture
}

enum class NavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, PERMANENT_NAVIGATION_DRAWER
}

@Composable
fun rememberBackgroundPainter(backgroundResource: Int): Painter {
    // TODO: Continue with the kt-coil replacement
//    return painterResource(id = backgroundResource)
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .decoderFactory(SvgDecoder.Factory())
            .data("android.resource://${LocalContext.current.applicationContext.packageName}/${backgroundResource}")
            .size(Size.ORIGINAL)
            .build()
    )
}

private val backgroundResources = listOf(
    R.raw.river,
    R.raw.lake,
    R.raw.mountains,
    R.raw.praire,
).shuffled()

@Composable
fun BackgroundImage(index: Int = 0) {
    Image(
        painter = rememberBackgroundPainter(
            backgroundResource = backgroundResources[index]
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.3F,
        modifier = Modifier.fillMaxSize()
    )
}