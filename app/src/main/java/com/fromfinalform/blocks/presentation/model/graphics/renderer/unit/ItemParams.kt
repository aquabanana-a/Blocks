/*
 * Created by S.Dobranos on 11.02.21 21:56
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.graphics.PointF
import android.graphics.RectF
import com.fromfinalform.blocks.common.clone

data class ItemParams(
    val dstRect: RectF,
    val srcRect: RectF,
    var angle: Float = 0f,
    var clipRect: RectF? = null,
    var anglePivot: PointF = PointF((dstRect.left + dstRect.right) / 2, (dstRect.bottom + dstRect.top) / 2) // TODO: implement multi pivot
) {
    fun centerPivot() = PointF((dstRect.left + dstRect.right) / 2, (dstRect.bottom + dstRect.top) / 2)

    fun copy(): ItemParams {
        return ItemParams(dstRect.clone(), srcRect.clone(), angle, clipRect?.clone(), anglePivot.clone())
    }
}