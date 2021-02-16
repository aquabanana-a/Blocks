/*
 * Created by S.Dobranos on 12.02.21 18:10
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import kotlin.math.floor

open class RotateTo(var destAngle: Float, val speed: Float, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : GLCompletableAnimation<RotateTo>(startTimeMs, interpolator) {

    private var valueTransformed: Float = 0f
    protected open var delta = 0f

    override fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long {
        delta = destAngle - item.rotation
        return startTimeMs + floor(delta / speed).toLong()
    }

    override fun transformImpl(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Boolean {
        var value = ((renderParams.timeMs - startTimeMs) / durationMs.toFloat()).coerceIn(0f, 1f)
        var valueInterpolated = interpolator.getInterpolation(value)
        var valueDelta = valueInterpolated - valueTransformed

        item.rotate(delta * valueDelta)

        valueTransformed = valueInterpolated
        return value >= 1.0f
    }

    override fun clone(): RotateTo {
        return RotateTo(destAngle, speed, startTimeMs, interpolator)
            .withOnComplete(completeHandler) as RotateTo
    }
}