package name.antonkonyshev.home.utils

import android.graphics.Rect
import androidx.window.layout.FoldingFeature

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