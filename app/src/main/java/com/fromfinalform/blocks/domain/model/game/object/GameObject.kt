/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`

import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject.Companion.clearRemoved
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimation
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

open class GameObject(val id: Long = GameObjectIndexer.getNext()) : ICloneable<GameObject> {

    companion object {
        fun MutableList<GameObject>.clearRemoved() {
            this.removeAll { it.isRemoved }
            this.forEach { (it.childs as? MutableList)?.clearRemoved() }
        }
    }

    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var rotation: Float = 0f
    var alpha: Float = 1f

    var assetId: Int? = null
    var color: Long? = null
    var textStyle: TextStyle? = null

    var tag: Any? = null
    var parent: GameObject? = null
    var childs: List<GameObject>? = null
    var animations: List<GameObjectAnimation>? = null

    var isWaitForRemove = false; private set
    var isRemoved = false; private set
    var isDirty = true; private set

    private val lo = Any()
    
    fun requestDraw(): GameObject { synchronized(lo) { // todo: sync? return to AtomicBoolean?
        this.isDirty = true
        return this
    } }
    
    fun onDrawn() { synchronized(lo) {
        this.isDirty = false
    } }

    fun requestRemove(): GameObject { synchronized(lo) {
        this.isWaitForRemove = true
        this.isDirty = true
        return this
    } }

    fun onRemoved() { synchronized(lo) {
        this.isWaitForRemove = false
        this.isRemoved = true
    } }

    fun translate(dX: Float, dY: Float): GameObject {
        this.translateX(dX)
        this.translateY(dY)
        return this
    }

    fun translateX(dX: Float): GameObject { synchronized(lo) {
        this.x += dX
        this.childs?.forEach { c -> c.translateX(dX) }
        return this
    } }

    fun translateY(dY: Float): GameObject { synchronized(lo) {
        this.y += dY
        this.childs?.forEach { c -> c.translateY(dY) }
        return this
    } }

    fun withLocation(x: Float, y: Float): GameObject {
        this.translateX(x - this.x)
        this.translateY(y - this.y)
        return this
    }

    fun add(value: GameObject): GameObject { synchronized(lo) {
        if (this.childs == null)
            this.childs = arrayListOf()
        value.parent = this
        (this.childs as MutableList).add(value)
        return this
    } }

    fun add(value: GameObjectAnimation): GameObject { synchronized(lo) {
        if (this.animations == null)
            this.animations = arrayListOf()
        (this.animations as MutableList).add(value)
        return this
    } }

    fun clearRemovedChilds() { synchronized(lo) {
        if (childs != null)
            (this.childs as MutableList).clearRemoved()
    } }

    override fun clone(): GameObject { synchronized(lo) {
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
        if (animations != null) ret.animations = animations!!.map { it.clone() }

        return ret
    } }
}