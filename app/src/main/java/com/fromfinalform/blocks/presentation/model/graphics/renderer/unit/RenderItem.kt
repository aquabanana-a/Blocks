/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.common.getOrCreate
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.presentation.model.graphics.animation.GLAnimationTimeline
import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLAnimation
import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLCompletableAnimation
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId

open class RenderItem(
    override val itemParams: ItemParams = ItemParams(
        RectF(-1f, 1f, -1f, 1f),
        RectF(0f, 0f, 1f, 1f)
    )) : IRenderItem {

    override var id: Long = -1L
    override var parent: IRenderItem?= null

    var x       get()  = itemParams.dstRect.left
                set(v) { val dx = v - itemParams.dstRect.left
                         itemParams.dstRect.left = v
                         itemParams.dstRect.right += dx
                         itemParams.anglePivot.x += dx }

    var y       get()  = itemParams.dstRect.top
                set(v) { val dy = itemParams.dstRect.top - v
                         itemParams.dstRect.top = v
                         itemParams.dstRect.bottom -= dy
                         itemParams.anglePivot.y -= dy }

    var width   get()  = itemParams.dstRect.width()
                set(v) { val dw = v - itemParams.dstRect.width()
                         itemParams.dstRect.right = x + v
                         itemParams.anglePivot.x += dw / 2 }

    var height  get()  = itemParams.dstRect.heightInv()
                set(v) { val dh = v - itemParams.dstRect.heightInv()
                         itemParams.dstRect.bottom = y - v
                         itemParams.anglePivot.y -= dh / 2 }

    var alpha   get()  = itemParams.alpha
                set(v) { itemParams.alpha = v }

    var rotation get()  = itemParams.angle
                 set(v) { itemParams.angle = v }

    var rotationPivot get()  = itemParams.anglePivot
                      set(v) { itemParams.anglePivot = v }

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

    var tag: Any? = null; private set

    private val lo = Any()
    override var childs: List<IRenderItem>? = null
    val timelines = hashMapOf<Long, GLAnimationTimeline>()

    val usedBlend get() = usedBlendFactor || usedBlendSeparate
    val usedBlendFactor get() = blendSrc > 0 && blendDst > 0
    val usedBlendSeparate get() = blendSrcRGB != null && blendSrcAlpha != null && blendDstRGB != null && blendDstAlpha != null

    fun translateX(dX: Float): RenderItem { synchronized(lo) {
        this.x += dX
        this.itemParams.onChanged()
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.translateX(dX) }
        return this
    } }

    fun translateY(dY: Float): RenderItem { synchronized(lo) {
        this.y -= dY
        this.itemParams.onChanged()
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.translateY(dY) }
        return this
    } }

    fun translateXY(dX: Float, dY: Float): RenderItem { synchronized(lo) {
        this.x += dX
        this.y -= dY
        this.itemParams.onChanged()
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.translateXY(dX, dY) }
        return this
    } }

    fun translateXWidth(times: Int): RenderItem {
        this.translateX(times * width)
        return this
    }

    fun translateYHeight(times: Int): RenderItem {
        this.translateY(times * height)
        return this
    }

    fun alphaSet(value: Float): RenderItem { synchronized(lo) {
        this.alpha = value
        this.itemParams.onChanged()
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { it.alphaSet(value) }
        return this
    } }

    fun alphaMul(value: Float): RenderItem { synchronized(lo) {
        this.alpha *= value
        this.itemParams.onChanged()
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { it.alphaMul(value) }
        return this
    } }

    fun rotate(dA: Float): RenderItem { synchronized(lo) {
        this.rotation += dA
        this.itemParams.onChanged()
        return this
    } }

    fun scaleX(factor: Float): RenderItem { synchronized(lo) {
        val dw = width*(factor - 1)
        this.x -= dw / 2f
        this.width += dw
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.scaleX(factor) }
        return this
    } }

    fun scaleY(factor: Float): RenderItem { synchronized(lo) {
        val dh = height * (factor - 1)
        this.y += dh / 2f
        this.height += dh
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.scaleY(factor) }
        return this
    } }

    fun scaleXY(factor: Float): RenderItem { synchronized(lo) {
        val dw = width * (factor - 1)
        val dh = height * (factor - 1)
        this.x -= dw / 2f
        this.y += dh / 2f
        this.width += dw
        this.height += dh
        this.childs?.mapNotNull { it as? RenderItem }?.forEach { c -> c.scaleXY(factor) }
        return this
    } }

    override fun addChild(value: IRenderItem) { synchronized(lo) {
        if(this.childs == null)
            this.childs = ArrayList()

        value.parent = this
        (this.childs as ArrayList).add(value)
    } }

    override fun removeChild(id: Long): IRenderItem? { synchronized(lo) {
        var item = (this.childs as? ArrayList)?.first { it.id == id }
        val removed = (this.childs as? ArrayList)?.remove(item)
        if (removed == true) {
            item?.parent = null
            return item
        }
        return null
    } }

    fun addAnimation(value: IGLAnimation, timelineId: Long = 0): GLAnimationTimeline? { synchronized(lo) {
        if (value is IGLCompletableAnimation) {
            var timeline = timelines.getOrCreate(timelineId, { GLAnimationTimeline(timelineId) })
            timeline.enqueue(value)
            return timeline
        }
        return null
    } }

//    fun removeAnimation(value: IGLAnimation) { synchronized(lo) {
//        (this.animations as? MutableList)?.remove(value)
//    } }

    fun withId(id: Long): RenderItem {
        this.id = id
        return this
    }

    fun setLayout(location: PointF, size: PointF, r: Float, a: Float) { synchronized(lo) {
        this.x = location.x
        this.y = location.y
        this.width = size.x
        this.height = size.y
        this.rotation = r
        this.alpha = a
    } }

    fun withLocation(x: Float, y: Float): RenderItem {
        this.translateX(x - this.x)
        this.translateY(this.y - y)
        return this
    }

    fun withRotation(angle: Float): RenderItem {
        this.rotation = angle
        this.itemParams.onChanged()
        return this
    }

    fun withSize(w: Float, h: Float): RenderItem {
        this.width = w
        this.height = h
        this.itemParams.onChanged()
        return this
    }

    fun withSrcRect(src: RectF): RenderItem {
        this.itemParams.srcRect.left = src.left
        this.itemParams.srcRect.top = src.top
        this.itemParams.srcRect.right = src.right
        this.itemParams.srcRect.bottom = src.bottom
        this.itemParams.onChanged()
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

    fun withChilds(values: List<IRenderItem>?): RenderItem { synchronized(lo) {
        this.childs = values?.map { c -> c.clone() }
        return this
    } }

//    fun withAnimations(values: List<IGLAnimation>?): RenderItem { synchronized(lo) {
//        this.animations = if (values == null) null else ArrayList(values.map { c -> c.clone() })
//        return this
//    } }

    fun withTag(value: Any? = null): RenderItem {
        this.tag = value
        return this
    }

    override fun clone(): RenderItem { synchronized(lo) {
        return RenderItem(itemParams.clone())
            .withTexture(textureId)
            .withColor(color, colorSecondary, colorAngle)
            .withShader(shaderTypeId)
            .withBlendFactor(blendSrc, blendDst)
            .withBlendSeparate(blendSrcRGB, blendDstRGB, blendSrcAlpha, blendDstAlpha)
            .withTag(tag)
            .withChilds(childs).also {
                it.parent = parent
            }
    } }
}