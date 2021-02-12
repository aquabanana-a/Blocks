/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.graphics.RectF
import android.opengl.GLES20
import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.common.clone
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId

open class RenderItem(
    val itemParams: ItemParams = ItemParams(
        RectF(-1f, 1f, -1f, 1f),
        RectF(0f, 0f, 1f, 1f)
    )) : IRenderItem, ICloneable<RenderItem> {

    override var id: Long = -1L

    var x       get()  = itemParams.dstRect.left
                set(v) { itemParams.dstRect.left = v }

    var y       get()  = itemParams.dstRect.top
                set(v) { itemParams.dstRect.top = v }

    var width   get()  = itemParams.dstRect.width()
                set(v) { itemParams.dstRect.right = x + v }

    var height  get()  = itemParams.dstRect.heightInv()
                set(v) { itemParams.dstRect.bottom = y - v }

    var rotation get()  = itemParams.angle
                 set(v) { itemParams.angle = v }

//    var x: Float = -1f;                             private set
//    var y: Float = 1f;                              private set
//    var width: Float = 0f;                          private set
//    var height: Float = 0f;                         private set
//    var rotation: Float = 0f;                       private set
//
//    var srcRect: RectF = RectF(0f, 0f, 1f, 1f);     private set
//    val dstRect: RectF get() = RectF(x, y, x + width, y - height)

    var textureId: Int? = null;                     private set
    var color: Long = 0xFF000000;                   private set
    var colorSecondary: Long = 0;                   private set
    var colorAngle: Int = 0;                        private set
    var shaderTypeId = ShaderDrawerTypeId.NONE;     private set

    var blendSrc = GLES20.GL_ONE;                   private set
    var blendDst = GLES20.GL_ONE_MINUS_SRC_ALPHA;   private set
    var blendSrcRGB: Int? = null;                   private set
    var blendSrcAlpha: Int? = null;                 private set
    var blendDstRGB: Int? = null;                   private set
    var blendDstAlpha: Int? = null;                 private set

    var childs: List<RenderItem>? = null;           private set
    private val childsLo = Any()

    val usedBlend get() = usedBlendFactor || usedBlendSeparate
    val usedBlendFactor get() = blendSrc > 0 && blendDst > 0
    val usedBlendSeparate get() = blendSrcRGB != null && blendSrcAlpha != null && blendDstRGB != null && blendDstAlpha != null

    fun translateX(dX: Float): RenderItem { synchronized(childsLo) {
        this.x += dX
        this.childs?.forEach { c -> c.translateX(dX) }
        return this
    } }

    fun translateXwidth(times: Int): RenderItem {
        this.translateX(times * width)
        return this
    }

    fun translateY(dY: Float): RenderItem { synchronized(childsLo) {
        this.y -= dY
        this.childs?.forEach { c -> c.translateY(dY) }
        return this
    } }

    fun translateYheight(times: Int): RenderItem {
        this.translateY(times * height)
        return this
    }

    fun addChild(value: RenderItem) { synchronized(childsLo) {
        if(this.childs == null)
            this.childs = ArrayList()

        (this.childs as ArrayList).add(value)
    } }

    fun removeChild(id: Long): RenderItem? { synchronized(childsLo) {
        var item = (this.childs as? ArrayList)?.first { it.id == id }
        val removed = (this.childs as? ArrayList)?.remove(item)
        return if (removed == true) item else null
    } }

    fun withId(id: Long): RenderItem {
        this.id = id
        return this
    }

    fun setLayout(x: Float, y: Float, w: Float, h: Float, r: Float) { synchronized(childsLo) {
        this.x = x
        this.y = y
        this.width = w
        this.height = h
        this.rotation = r
    } }

    fun withLocation(x: Float, y: Float): RenderItem {
        this.x = x
        this.y = y
//        this.translateX(x + this.x)
//        this.translateY(y + this.y)
        return this
    }

    fun withRotation(angle: Float): RenderItem {
        this.rotation = angle
        return this
    }

    fun withSize(w: Float, h: Float): RenderItem {
        this.width = w
        this.height = h
        return this
    }

    fun withSrcRect(src: RectF): RenderItem {
        this.itemParams.srcRect.left = src.left
        this.itemParams.srcRect.top = src.top
        this.itemParams.srcRect.right = src.right
        this.itemParams.srcRect.bottom = src.bottom
        return this
    }

    fun withTexture(textureId: Int?): RenderItem {
        this.textureId = textureId
        return this
    }

    fun withColor(color: Long, colorSecondary: Long = -1, angle: Int = 0): RenderItem {
        this.color = color
        this.colorSecondary = colorSecondary
        this.colorAngle = angle
        return this
    }

    fun withShader(shaderTypeId: ShaderDrawerTypeId): RenderItem {
        this.shaderTypeId = shaderTypeId
        return this
    }

    fun withBlendSeparate(srcRGB: Int?, dstRGB: Int?, srcAlpha: Int?, dstAlpha: Int?): RenderItem {
        this.blendSrcRGB = srcRGB
        this.blendSrcAlpha = srcAlpha
        this.blendDstRGB = dstRGB
        this.blendDstAlpha = dstAlpha
        return this
    }

    fun withBlendFactor(blendSrc: Int, blendDst: Int): RenderItem {
        this.blendSrc = blendSrc
        this.blendDst = blendDst
        return this
    }

    fun withChilds(items: List<RenderItem>?): RenderItem {
        this.childs = if(items == null) null else ArrayList(items.map { c -> c.clone() })
        return this
    }

    override fun clone(): RenderItem { synchronized(childsLo) {
        return RenderItem(itemParams.copy())
            .withTexture(textureId)
            .withColor(color, colorSecondary, colorAngle)
            .withShader(shaderTypeId)
            .withBlendFactor(blendSrc, blendDst)
            .withBlendSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha)
            .withChilds(childs)
    } }
}