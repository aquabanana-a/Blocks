/*
 * Created by S.Dobranos on 12.02.21 18:10
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.graphics.PointF
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.common.clone
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class TranslateTo(val destXY: PointF, val speed: Float, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : GLCompletableAnimation<TranslateTo>(startTimeMs, interpolator) {

    private var valueTransformed: Float = 0f
    private var distanceX = 0f
    private var distanceY = 0f

    override fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long {
        distanceX = destXY.x - item.x
        distanceY = item.y - destXY.y
        var distance = sqrt(distanceX.pow(2) + distanceY.pow(2))

        return startTimeMs + floor(distance / speed).toLong()
    }

    override fun transformImpl(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Boolean {
        var value = ((renderParams.timeMs - startTimeMs) / durationMs.toFloat()).coerceIn(0f, 1f)
        var valueInterpolated = interpolator.getInterpolation(value)
        var valueDelta = valueInterpolated - valueTransformed

        item.translateXY(distanceX * valueDelta, distanceY * valueDelta)

        valueTransformed = valueInterpolated
        return value >= 1.0f
    }

    override fun clone(): TranslateTo {
        return TranslateTo(destXY.clone(), speed, startTimeMs, interpolator)
    }
}