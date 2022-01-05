/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`

import android.graphics.PointF
import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimation
import com.fromfinalform.blocks.presentation.model.graphics.animation.GLAnimationTimeline
import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLTimelineQueueCompleteEvent
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle
import java.util.concurrent.atomic.AtomicBoolean

open class GameObject(val id: Long = GameObjectIndexer.getNext()) : ICloneable<GameObject> {

    companion object {
        fun List<GameObject?>?.removeTrash() { tryRemoveTrashFromList(this) }

        fun tryRemoveTrashFromList(src: List<GameObject?>?) {
            if ((src as? MutableList) != null) {
                src.removeAll { it?.isRemoved == true }
                src.forEach { if (it?.childs != null) tryRemoveTrashFromList(it.childs as? MutableList) }
            }
        }
    }

    var x: Float = 0f
    var y: Float = 0f
    val location: PointF get() = PointF(x, y)

    var width: Float = 0f
    var height: Float = 0f
    val size: PointF get() = PointF(width, height)

    var rotation: Float = 0f
    var alpha: Float = 1f

    var assetId: Int? = null
    var color: Long? = null
    var textStyle: TextStyle? = null

    var tag: Any? = null
    var parent: GameObject? = null
    var childs: List<GameObject>? = null
    var animations: List<GameObjectAnimation>? = null

    var drawedHandler: ((GameObject)->Unit)? = null
    var removedHandler: ((GameObject)->Unit)? = null

    var animationQueueCompleteHandler: ((GameObject, GLTimelineQueueCompleteEvent) -> Boolean)? = null

    var isWaitForAnimate = false; private set
    var isWaitForRemove = false; private set
    var isRemoved = false; private set
    var isDirty = true; private set

//    private var canMergeImpl = AtomicBoolean(false)
//    val canMerge get() = canMergeImpl.get()
    var canMerge = false

    private val lo = Any()

    fun enableMerge(): GameObject {
//        this.canMergeImpl.set(true)
        this.canMerge = true
        return this
    }

    fun disableMerge(): GameObject {
//        this.canMergeImpl.set(false)
        this.canMerge = false
        return this
    }

    fun withOnAnimationQueueComplete(handler: ((GameObject, GLTimelineQueueCompleteEvent) -> Boolean)? = null): GameObject {
        this.animationQueueCompleteHandler = handler
        return this
    }

    fun withOnDrawed(handler: ((GameObject) -> Unit)? = null): GameObject {
        this.drawedHandler = handler
        return this
    }

    fun requestDraw(): GameObject { /*synchronized(lo) {*/ // todo: sync? return to AtomicBoolean?
        this.isDirty = true
        return this
    } /*}*/
    
    fun onDrawn() { /*synchronized(lo) {*/
        this.isDirty = false
        this.drawedHandler?.invoke(this)
    } /*}*/

    fun withOnRemoved(handler: ((GameObject) -> Unit)? = null): GameObject {
        this.removedHandler = handler
        return this
    }

    fun requestRemove(): GameObject { /*synchronized(lo) {*/
        this.isWaitForRemove = true
        this.isDirty = true
        return this
    } /*}*/

    fun onRemoved() { /*synchronized(lo) {*/
        this.isWaitForRemove = false
        this.isRemoved = true
        this.removedHandler?.invoke(this)
    } /*}*/

    fun translate(dX: Float, dY: Float): GameObject {
        this.translateX(dX)
        this.translateY(dY)
        return this
    }

    fun translateX(dX: Float): GameObject { /*synchronized(lo) {*/
        this.x += dX
        this.childs?.forEach { c -> c.translateX(dX) }
        return this
    } /*}*/

    fun translateY(dY: Float): GameObject { /*synchronized(lo) {*/
        this.y += dY
        this.childs?.forEach { c -> c.translateY(dY) }
        return this
    } /*}*/

    fun withLocation(x: Float, y: Float): GameObject {
        this.translateX(x - this.x)
        this.translateY(y - this.y)
        return this
    }

    fun requestAnimation(value: GameObject): GameObject { /*synchronized(lo) {*/
        if (this.childs == null)
            this.childs = arrayListOf()
        value.parent = this
        (this.childs as MutableList).add(value)
        return this
    } /*}*/

    fun requestAnimation(value: GameObjectAnimation): GameObject { /*synchronized(lo) {*/
        if (this.animations == null)
            this.animations = arrayListOf()
        (this.animations as MutableList).add(value)
        this.isWaitForAnimate = true
        requestDraw()
        return this
    } /*}*/

    fun onAnimationConsumed(value: GameObjectAnimation): GameObject { /*synchronized(lo) {*/
        if (this.animations?.isNotEmpty() == false) {
            this.isWaitForAnimate = false
            return this
        }

        (this.animations as MutableList).remove(value)
        if (this.animations?.isEmpty() == true)
            this.isWaitForAnimate = false

        return this
    } /*}*/

    fun clearRemovedChilds() { /*synchronized(lo) {*/
        childs.removeTrash()
    } /*}*/

    override fun clone(): GameObject { /*synchronized(lo) {*/
        val ret = GameObject()
        ret.x = x
        ret.y = y
        ret.width = width
        ret.height = height
        ret.assetId = assetId
        ret.color = color
        ret.textStyle = textStyle
        ret.parent = parent

        if (childs != null) ret.childs = childs!!.map { it.clone() }
        //if (animations != null) ret.animations = animations!!.map { it.clone() }

        return ret
    } /*}*/
}