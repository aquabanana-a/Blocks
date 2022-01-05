package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import kotlin.math.floor
import kotlin.math.max

open class ScaleXY(
    val xScaleStart: Float,
    val xScaleEnd: Float,
    val yScaleStart: Float,
    val yScaleEnd: Float,
    override var durationMs: Long, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : GLCompletableAnimation<ScaleXY>(startTimeMs, interpolator) {

    private var scalexTransformed = 1f
    private var scaleyTransformed = 1f

    private var affectChilds = false

    var pivot = Pivot.Center; private set
    enum class Pivot {
        Center,
        CenterLeft,
        CenterTop,
        CenterRight,
        CenterBottom,
        Corner_TL,
        Corner_TR,
        Corner_BL,
        Corner_BR
    }

    fun withPivot(value: Pivot): ScaleXY {
        this.pivot = value
        return this
    }

    fun withAffectChilds(value: Boolean = true/*TODO: implement rules*/): ScaleXY {
        this.affectChilds = value
        return this
    }

    override fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long {
        return startTimeMs + durationMs
    }

    override fun transformImpl(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Boolean {
        var value = ((renderParams.timeMs - startTimeMs) / durationMs.toFloat()).coerceIn(0f, 1f)
        var valueInterpolated = interpolator.getInterpolation(value)



        val scalex = xScaleEnd * valueInterpolated + xScaleStart * (1f - valueInterpolated)
        val scaley = yScaleEnd * valueInterpolated + yScaleStart * (1f - valueInterpolated)

        val scalexCurr = scalex / scalexTransformed
        val scaleyCurr = scaley / scaleyTransformed

        transformItem(item, scalexCurr, scaleyCurr)
        if (affectChilds)
            item.childs?.mapNotNull { it as? RenderItem }?.forEach { transformItem(it, scalexCurr, scaleyCurr) }

        scalexTransformed = scalex
        scaleyTransformed = scaley

        return value >= 1.0f
    }

    private fun transformItem(item: RenderItem, scalexCurr: Float, scaleyCurr: Float) {
        val dst = item.itemParams.dstRect
        val clip = item.itemParams.clipRect

        var width = dst.width() * scalexCurr
        var height = dst.heightInv() * scaleyCurr
        val sumx = dst.right + dst.left
        val sumy = dst.top + dst.bottom

        val widthDst = dst.right - dst.left
        val widthDstScaled = widthDst * scalexCurr
        var widthDstOutsideClipLeft = 0f
        var widthDstOutsideClipLeftPerc = 0f
        var widthDstOutsideClipRight = 0f
        var widthDstOutsideClipRightPerc = 0f

        val heightDst = dst.top - dst.bottom
        val heightDstScaled = heightDst * scaleyCurr
        var heightDstOutsideClipTop = 0f
        var heightDstOutsideClipTopPerc = 0f
        var heightDstOutsideClipBottom = 0f
        var heightDstOutsideClipBottomPerc = 0f

        if (clip != null && pivot != Pivot.Center) {
            widthDstOutsideClipLeft = clip.left - dst.left
            widthDstOutsideClipLeftPerc = widthDstOutsideClipLeft / widthDst
            widthDstOutsideClipRight = clip.right - dst.right
            widthDstOutsideClipRightPerc = widthDstOutsideClipRight / widthDst

            heightDstOutsideClipTop = clip.top - dst.top
            heightDstOutsideClipTopPerc = heightDstOutsideClipTop / heightDst
            heightDstOutsideClipBottom = clip.bottom - dst.bottom
            heightDstOutsideClipBottomPerc = heightDstOutsideClipBottom / heightDst
        }

        with(dst) {
            when (pivot) {
                Pivot.Center -> {
                    left = (sumx - width) * .5f
                    right = (sumx + width) * .5f
                    top = (sumy + height) * .5f
                    bottom = (sumy - height) * .5f
                }
                Pivot.CenterLeft -> {
                    right = left + widthDstScaled
                    top = (sumy + height) * .5f
                    bottom = (sumy - height) * .5f
                }
                Pivot.CenterTop -> {
                    left = (sumx - width) * .5f
                    right = (sumx + width) * .5f
                    bottom = top - heightDstScaled
                }
                Pivot.CenterRight -> {
                    left = right - widthDstScaled
                    top = (sumy + height) * .5f
                    bottom = (sumy - height) * .5f
                }
                Pivot.CenterBottom -> {
                    left = (sumx - width) * .5f
                    right = (sumx + width) * .5f
                    top = bottom + heightDstScaled
                }
                Pivot.Corner_TL -> {
                    right = left + widthDstScaled
                    bottom = top - heightDstScaled
                }
                Pivot.Corner_TR -> {
                    left = right - widthDstScaled
                    bottom = top - heightDstScaled
                }
                Pivot.Corner_BL -> {
                    right = left + widthDstScaled
                    top = bottom + heightDstScaled
                }
                Pivot.Corner_BR -> {
                    left = right - widthDstScaled
                    top = bottom + heightDstScaled
                }
            }
        }

        if (clip != null)
            with(clip) {
                val sumClipX = clip.right + clip.left
                val sumClipY = clip.top + clip.bottom
                val clipWidth = (clip.right - clip.left) * scalexCurr
                val clipHeight = (clip.top - clip.bottom) * scaleyCurr

                when (pivot) {
                    Pivot.Center -> {
                        left = (sumClipX - clipWidth) * .5f
                        right = (sumClipX + clipWidth) * .5f
                        top = (sumClipY + clipHeight) * .5f
                        bottom = (sumClipY - clipHeight) * .5f
                    }
                    Pivot.CenterBottom -> {
                        left = (sumClipX - clipWidth) * .5f
                        right = (sumClipX + clipWidth) * .5f
                        top = bottom + clipHeight
                    }
                    Pivot.CenterTop -> {
                        left = (sumClipX - clipWidth) * .5f
                        right = (sumClipX + clipWidth) * .5f
                        bottom = top - clipHeight
                    }
                    Pivot.Corner_TL -> {
                        right = left + clipWidth
                        bottom = top - clipHeight
                    }
                    Pivot.Corner_TR -> {
                        left = right - clipWidth
                        bottom = top - clipHeight
                    }
                    Pivot.Corner_BL -> {
                        right = left + clipWidth
                        top = bottom + clipHeight
                    }
                    Pivot.Corner_BR -> {
                        left = right - clipWidth
                        top = bottom + clipHeight
                    }
                }

                if (pivot != Pivot.Center) {
                    dst.left = left - widthDstScaled * widthDstOutsideClipLeftPerc
                    dst.right = right - widthDstScaled * widthDstOutsideClipRightPerc
                    dst.top = top - heightDstScaled * heightDstOutsideClipTopPerc
                    dst.bottom = bottom - heightDstScaled * heightDstOutsideClipBottomPerc
                }

                item.itemParams.onChanged()
            }
    }

    override fun clone(): ScaleXY {
        return ScaleXY(xScaleStart, xScaleEnd, yScaleStart, yScaleEnd, durationMs, startTimeMs, interpolator)
            .withPivot(pivot)
            .withAffectChilds(affectChilds)
    }
}