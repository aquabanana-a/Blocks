package com.fromfinalform.blocks.presentation.model.graphics.animation

import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLAnimationCompleteArgs
import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLAnimationCompleteEvent
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import io.reactivex.rxjava3.subjects.AsyncSubject

interface IGLCompletableAnimation : IGLAnimation {
    val completeEvent: AsyncSubject<GLAnimationCompleteEvent>

    val isComplete: Boolean
    fun onComplete(args: GLAnimationCompleteArgs)
    fun withOnComplete(value: ((GLAnimationCompleteEvent) -> Unit)?): IGLCompletableAnimation
}