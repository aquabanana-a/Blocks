/*
 * Created by S.Dobranos on 10.02.21 23:42
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import java.lang.IllegalStateException

abstract class GLCompletableAnimation<T>(
    var startTimeMs: Long,
    val interpolator: Interpolator) : IGLCompletableAnimation {

    override var isInitialized = false; protected set
    override var isComplete = false; protected set

    open var endTimeMs: Long = startTimeMs; protected set
    open var durationMs: Long = 0L; protected set

    final override var completeHandler: ((renderItem: RenderItem, renderParams: RenderParams, sceneParams: SceneParams) -> Unit)?= null
    fun withOnComplete(value: ((renderItem: RenderItem, renderParams: RenderParams, sceneParams: SceneParams) -> Unit)?): IGLCompletableAnimation {
        this.completeHandler = value
        return this
    }

    final override fun initialize(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams) {
        if (isInitialized)
            return

        startTimeMs += renderParams.timeMs
        endTimeMs = prepare(item, renderParams, sceneParams)
        if (endTimeMs < startTimeMs)
            throw IllegalStateException()

        durationMs = endTimeMs - startTimeMs
        isInitialized = true
    }

    final override fun transform(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams) {
        if (isComplete || renderParams.timeMs < startTimeMs)
            return

        isComplete = transformImpl(item, renderParams, sceneParams)
    }

    override fun clone(): GLCompletableAnimation<T> {
        TODO("Not yet implemented")
    }

    protected open fun prepare(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Long = startTimeMs

    protected abstract fun transformImpl(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams): Boolean
}