/*
 * Created by S.Dobranos on 07.02.21 21:39
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

open class GameObject(val id: Long = GameObjectIndexer.getNext()) : ICloneable<GameObject> {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var rotation: Float = 0f

    var assetId: Int? = null
    var color: Long? = null
    var textStyle: TextStyle? = null

    var childs: List<GameObject>? = null

    fun translate(dX: Float, dY: Float): GameObject {
        this.translateX(dX)
        this.translateY(dY)
        return this
    }

    fun translateX(dX: Float): GameObject {
        this.x += dX
        this.childs?.forEach { c -> c.translateX(dX) }
        return this
    }

    fun translateY(dY: Float): GameObject {
        this.y -= dY
        this.childs?.forEach { c -> c.translateY(dY) }
        return this
    }

    override fun clone(): GameObject {
        val ret = GameObject()
        ret.x = x
        ret.y = y
        ret.width = width
        ret.height = height
        ret.assetId = assetId
        ret.color = color
        ret.textStyle = textStyle

        if (childs != null)
            ret.childs = childs!!.map { it.clone() }

        return ret
    }
}