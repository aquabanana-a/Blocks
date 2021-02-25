/*
 * Created by S.Dobranos on 25.02.21 12:14
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLAnimationCompleteEvent
import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLTimelineQueueCompleteEvent
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class GLAnimationTimeline(val id: Long) {

    private val lo = Any()
    private val animations = arrayListOf<IGLCompletableAnimation>()
    private val queueCompleteEvent = PublishSubject.create<GLTimelineQueueCompleteEvent>()

    val currentAnimation get() = synchronized(lo) { animations.firstOrNull() }

    fun enqueue(value: IGLCompletableAnimation) { synchronized(lo) {
        animations.add(value.withOnComplete { onAnimationComplete(it) })
    } }

    fun withOnQueueComplete(handler: ((GLTimelineQueueCompleteEvent)->Boolean)): GLAnimationTimeline {
        var subscription: Disposable? = null
        subscription = queueCompleteEvent.subscribe { if (handler.invoke(it)) subscription?.dispose() }
        return this
    }

    private fun onAnimationComplete(e: GLAnimationCompleteEvent) { synchronized(lo) {
        if (animations.remove(e.src))
            queueCompleteEvent.onNext(GLTimelineQueueCompleteEvent(this))
    } }
}