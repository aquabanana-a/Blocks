/*
 * Created by S.Dobranos on 19.02.21 14:23
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`.animation

import android.graphics.PointF
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.common.ICloneable

class GameObjectAnimation(val typeId: GameObjectAnimationTypeId) : ICloneable<GameObjectAnimation> {
    companion object {
        const val PARAM_DEST_XY         = "dstXY"
        const val PARAM_DELAY           = "delay"
        const val PARAM_SPEED           = "speed"
        const val PARAM_INTERPOLATOR    = "interpolator"
    }

    var params = hashMapOf<String, Any>()

    val dstXY           get() = (params[PARAM_DEST_XY] as? PointF) ?: PointF()
    val delay           get() = (params[PARAM_DELAY] as? Long) ?: 0L
    val speed           get() = (params[PARAM_SPEED] as? Float) ?: 0.00001f
    val interpolator    get() = (params[PARAM_INTERPOLATOR] as? Interpolator) ?: LinearInterpolator()

    fun withParam(name: String, value: Any): GameObjectAnimation {
        this.params[name] = value
        return this
    }

    override fun clone(): GameObjectAnimation {
        val ret = GameObjectAnimation(typeId)
        for ((k, v) in params)
            ret.params[k] = if (v is ICloneable<*>) v.clone() as Any else v
        return ret
    }
}