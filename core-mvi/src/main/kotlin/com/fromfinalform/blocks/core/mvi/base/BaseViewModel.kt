package com.fromfinalform.blocks.core.mvi.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S: State> : ViewModel(), LifecycleEventObserver {

    protected val backgroundScope = CoroutineScope(IO + SupervisorJob())

    abstract val defaultState: S

    protected open val effectBufferSize = EFFECT_BUFFER_SIZE_DEFAULT

    protected val stateFlow by lazy { MutableStateFlow(defaultState) }
    protected val effectChannel by lazy { Channel<Effect>(effectBufferSize) }
    protected open val eventFlow: MutableSharedFlow<Event>? = null

    override fun onCleared() {
        super.onCleared()
        backgroundScope.coroutineContext.cancelChildren()
    }

    open fun bind(foregroundScope: LifecycleCoroutineScope, view: BaseView) {
        with(foregroundScope) {
            launch {
                stateFlow
                    .onEach(view::renderState)
                    .catch { /* TODO */ }
                    .collect()
            }

            launch {
                effectChannel
                    .receiveAsFlow()
                    .onEach(view::handleEffect)
                    .catch { /* TODO */ }
                    .collect()
            }
        }
    }

    protected fun applyState(state: S) {
        stateFlow.value = state
    }

    protected fun putEffect(effect: Effect) {
        with(backgroundScope) {
            launch {
                effectChannel.send(effect)
            }
        }
    }

    fun putEvent(event: Event) {
        with(backgroundScope) {
            launch {
                eventFlow?.emit(event)
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) { }
}