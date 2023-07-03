package name.antonkonyshev.home.utils

import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size

fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

sealed interface DevicePosture {
    object NormalPosture: DevicePosture

    data class BookPosture(
        val hingePosition: Rect?
    ): DevicePosture

    data class Separating(
        val hingePosition: Rect?,
        var orientation: FoldingFeature.Orientation?
    ): DevicePosture
}

enum class HomeNavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, PERMANENT_NAVIGATION_DRAWER
}

@Composable
fun getBackgroundPainter(backgroundResource: Int): Painter {
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .decoderFactory(SvgDecoder.Factory())
            .data("android.resource://${LocalContext.current.applicationContext.packageName}/${backgroundResource}")
            .size(Size.ORIGINAL)
            .build()
    )
}