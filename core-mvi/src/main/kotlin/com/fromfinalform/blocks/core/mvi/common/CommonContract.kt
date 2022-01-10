package com.fromfinalform.blocks.core.mvi.common

import com.fromfinalform.blocks.core.mvi.base.Effect
import com.fromfinalform.blocks.core.mvi.base.Event
import com.fromfinalform.blocks.core.mvi.base.State
import androidx.annotation.IdRes
import java.io.Serializable

const val COMMON_NAVIGATION_DATA_KEY = "common_navigation_data"

open class CommonState : State

open class CommonEffect : Effect {
    object NavigateBack : CommonEffect()
    data class NavigateTo(
        @IdRes val screenId: Int,
        @IdRes val hostId: Int? = null,
        val data: Serializable? = null) : CommonEffect()
}

open class CommonEvent : Event