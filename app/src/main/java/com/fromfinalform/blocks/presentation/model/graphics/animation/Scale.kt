/*
 * Created by S.Dobranos on 15.02.21 19:52
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator

class Scale(scaleTo: Float, scaleFrom: Float = 1.0f, durationMs: Long, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : ScaleXY(scaleFrom, scaleTo, scaleFrom, scaleTo, durationMs, startTimeMs, interpolator){
}