package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import kotlin.math.floor

class RotateBy(override var delta: Float, speed: Float, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : RotateTo(0f, speed, startTimeMs, interpolator) {

    override fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long {
        return startTimeMs + floor(delta / speed).toLong()
    }

    override fun clone(): RotateBy {
        return RotateBy(delta, speed, startTimeMs, interpolator)
    }
}