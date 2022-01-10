package com.fromfinalform.blocks.core.mvi.base

interface BaseView {

    suspend fun renderState(state: State)

    suspend fun handleEffect(effect: Effect)
}