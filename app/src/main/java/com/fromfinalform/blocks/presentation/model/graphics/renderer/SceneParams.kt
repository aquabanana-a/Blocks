package com.fromfinalform.blocks.presentation.model.graphics.renderer

import android.graphics.PointF
import kotlin.math.max
import kotlin.math.min

data class SceneParams(
    var sceneWidth: Float,
    var sceneHeight: Float,
    var surfaceWidth: Float,
    var surfaceHeight: Float,
    var scale: Float
) {
    constructor(size: ISize, surfaceWidth: Float, surfaceHeight: Float, scale: Float) : this(
        size.width,
        size.height,
        surfaceWidth,
        surfaceHeight,
        scale
    )

    var sx: Float = 2f / sceneWidth
    var sy: Float = 2f / sceneHeight
    var scaledSceneWidth = sceneWidth * scale
    var scaledSceneHeight = sceneHeight * scale
    var screen2glx = 2f / scaledSceneWidth
    var screen2gly = 2f / scaledSceneHeight
    var relativeScale = 1f

    var scaleInv: Float = 1f; private set

    val sceneWH: PointF get() = PointF(sceneWidth, sceneHeight)
    val isVertical: Boolean get() = scaledSceneHeight > scaledSceneWidth
    val isHorizontal: Boolean get() = scaledSceneWidth > scaledSceneHeight

    fun update(size: ISize, surfaceWidth: Float, surfaceHeight: Float) {

        scale = min(surfaceWidth / size.width, surfaceHeight / size.height)
        scaleInv = max(size.width / surfaceWidth, size.height / surfaceHeight)

        this.surfaceWidth = surfaceWidth
        this.surfaceHeight = surfaceHeight
        sceneWidth = size.width
        sceneHeight = size.height
        sx = 2f / sceneWidth
        sy = 2f / sceneHeight
        scaledSceneWidth = sceneWidth * scale
        scaledSceneHeight = sceneHeight * scale
        screen2glx = 2f / scaledSceneWidth
        screen2gly = 2f / scaledSceneHeight
    }

}