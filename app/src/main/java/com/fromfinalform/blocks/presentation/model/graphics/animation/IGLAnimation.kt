package com.fromfinalform.blocks.presentation.model.graphics.animation

import com.fromfinalform.blocks.common.ICloneable
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem

interface IGLAnimation : ICloneable<IGLAnimation> {
    val isInitialized: Boolean

    fun initialize(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams)
    fun transform(item: RenderItem, renderParams: RenderParams, sceneParams: SceneParams)
}