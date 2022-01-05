package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem

class Alpha(var alphaEnd: Float, var alphaStart: Float = START_VALUE_CURRENT, override var durationMs: Long, startTimeMs: Long = 0L, interpolator: Interpolator = LinearInterpolator())
    : GLCompletableAnimation<Alpha>(startTimeMs, interpolator) {

    companion object {
        const val START_VALUE_CURRENT = Float.MIN_VALUE + 1
    }

    private var affectChilds = true
    fun withAffectChilds(value: Boolean = true): Alpha {
        this.affectChilds = value
        return this
    }

    override fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long {
        if (alphaStart == START_VALUE_CURRENT)
            alphaStart = item.alpha
        return startTimeMs + durationMs
    }

    override fun transformImpl(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Boolean {
        var value = ((renderParams.timeMs - startTimeMs) / durationMs.toFloat()).coerceIn(0f, 1f)
        var valueInterpolated = interpolator.getInterpolation(value)

        item.alpha = alphaStart + (alphaEnd - alphaStart) * valueInterpolated

        if (affectChilds)
            item.childs?.mapNotNull { it as? RenderItem }?.forEach { transformImpl(it, renderParams, sceneParams) }

        return value >= 1.0f
    }

    override fun clone(): Alpha {
        return Alpha(alphaStart, alphaEnd, durationMs, startTimeMs, interpolator)
            .withAffectChilds(affectChilds)
    }
}