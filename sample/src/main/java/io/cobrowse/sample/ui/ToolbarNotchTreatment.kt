package io.cobrowse.sample.ui

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath

/**
 * [EdgeTreatment] that can be used to crop a toolbar's background to show the menu items only.
 * The called is responsible to calculate the visible width and set it via [toolbarMenuWidth].
 */
class ToolbarNotchTreatment(
    private val toolbarHeight: Float,
    private val toolbarMenuWidth: Float,
    private val radius: Float,
    private val cutToolbarHeight: Float = 0f,
    private val mirror: Boolean = false
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        center: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        if (length <= 0f) {
            super.getEdgePath(length, center, interpolation, shapePath)
            return
        }

        val horizontalAnchor = toolbarMenuWidth
        val topAnchor = toolbarHeight - cutToolbarHeight

        shapePath.addArc(
            horizontalAnchor - radius * 2,
            0f,
            horizontalAnchor,
            radius * 2,
            270f,
            90f)
        shapePath.addArc(
            horizontalAnchor,
            topAnchor - radius * 2,
            horizontalAnchor + radius * 2,
            topAnchor,
            180f,
            -90f)
        if (mirror) {
            shapePath.addArc(
                length - (horizontalAnchor + radius * 2),
                topAnchor - radius * 2,
                length - (horizontalAnchor),
                topAnchor,
                90f,
                -90f
            )
            shapePath.addArc(
                length - (horizontalAnchor),
                0f,
                length - (horizontalAnchor - radius * 2),
                radius * 2,
                180f,
                90f
            )
        } else {
            shapePath.lineTo(length, toolbarHeight)
        }
    }
}