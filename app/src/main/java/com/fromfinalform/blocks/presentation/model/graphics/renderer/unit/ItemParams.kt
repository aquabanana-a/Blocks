/*
 * Created by S.Dobranos on 11.02.21 21:56
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.graphics.PointF
import android.graphics.RectF
import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.common.clone

class ItemParams(
    val dstRect: RectF,
    val srcRect: RectF,
    var angle: Float = 0f,
    var alpha: Float = 1f,
    var clipRect: RectF? = null,
    var anglePivot: PointF = PointF((dstRect.left + dstRect.right) / 2, (dstRect.bottom + dstRect.top) / 2)) : ICloneable<ItemParams> { // TODO: implement multi pivot

    fun centerPivot() = PointF((dstRect.left + dstRect.right) / 2, (dstRect.bottom + dstRect.top) / 2)

    private var changedHandler: ((ItemParams)->Unit)? = null
    fun withOnChanged(handler: ((ItemParams)->Unit)? = null): ItemParams {
        this.changedHandler = handler
        return this
    }

    fun onChanged() {
        this.changedHandler?.invoke(this)
    }

    override fun clone(): ItemParams {
        return ItemParams(dstRect.clone(), srcRect.clone(), angle, alpha, clipRect?.clone(), anglePivot.clone())
            .withOnChanged(changedHandler)
    }
}