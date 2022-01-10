package com.fromfinalform.blocks.core.mvi.common

import com.fromfinalform.blocks.core.mvi.base.BaseView
import com.fromfinalform.blocks.core.mvi.base.BaseViewModel
import com.fromfinalform.blocks.core.mvi.base.Event
import androidx.lifecycle.LifecycleCoroutineScope
import com.fromfinalform.blocks.core.mvi.base.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class CommonViewModel: CommonStatedViewModel<CommonState>()

abstract class CommonStatedViewModel<S: State>: BaseViewModel<S>() {

    override val eventFlow: MutableSharedFlow<Event> = MutableSharedFlow()

    override fun bind(foregroundScope: LifecycleCoroutineScope, view: BaseView) {
        super.bind(foregroundScope, view)

        with(backgroundScope) {
            launch {
                eventFlow
                    .onEach(::handleEvent)
                    .flowOn(Dispatchers.Default)
                    .catch { /* TODO */ }
                    .collect()
            }
        }
    }

    protected open suspend fun handleEvent(event: Event) {}
}