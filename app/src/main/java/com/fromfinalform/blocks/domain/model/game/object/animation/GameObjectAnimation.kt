package com.fromfinalform.blocks.domain.model.game.`object`.animation

import android.graphics.PointF
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.common.ICloneable

class GameObjectAnimation(val typeId: GameObjectAnimationTypeId) : ICloneable<GameObjectAnimation> {
    companion object {
        const val PARAM_DEST_XY         = "dst_xy"
        const val PARAM_DEST_ANGLE      = "dst_angle"
        const val PARAM_DELAY           = "delay"
        const val PARAM_SPEED           = "speed"
        const val PARAM_INTERPOLATOR    = "interpolator"
        const val PARAM_DURATION        = "duration"
        const val PARAM_SCALE_FROM      = "scale_from"
        const val PARAM_SCALE_TO        = "scale_to"
        const val PARAM_AFFECT_CHILDS   = "affect_childs"
        const val PARAM_TIMELINE_ID     = "timeline_id"
    }

    var params = hashMapOf<String, Any>()
    var completeHandler: ((Long) -> Unit)? = null; private set

    val dstXY           get() = (params[PARAM_DEST_XY] as? PointF) ?: PointF()
    val dstAngle        get() = (params[PARAM_DEST_ANGLE] as? Float) ?: 0f
    val delay           get() = (params[PARAM_DELAY] as? Long) ?: (params[PARAM_DELAY] as? Int)?.toLong() ?: 0L
    val speed           get() = (params[PARAM_SPEED] as? Float) ?: 0.00001f
    val interpolator    get() = (params[PARAM_INTERPOLATOR] as? Interpolator) ?: LinearInterpolator()
    val duration        get() = (params[PARAM_DURATION] as? Long) ?: (params[PARAM_DURATION] as? Int)?.toLong() ?: 0L
    val scaleFrom       get() = (params[PARAM_SCALE_FROM] as? Float) ?: 0f
    val scaleTo         get() = (params[PARAM_SCALE_TO] as? Float) ?: 0f
    val affectChilds    get() = params.containsKey(PARAM_AFFECT_CHILDS)
    val timelineId      get() = (params[PARAM_TIMELINE_ID] as? Long) ?: (params[PARAM_TIMELINE_ID] as? Int)?.toLong() ?: 0L

    fun withParam(name: String, value: Any = Any()): GameObjectAnimation {
        this.params[name] = value
        return this
    }

    fun withOnComplete(handler: ((gameObjectId: Long)->Unit)? = null): GameObjectAnimation {
        this.completeHandler = handler
        return this
    }

    override fun clone(): GameObjectAnimation {
        val ret = GameObjectAnimation(typeId)
        for ((k, v) in params)
            ret.params[k] = if (v is ICloneable<*>) v.clone() as Any else v
        ret.completeHandler = completeHandler
        return ret
    }
}