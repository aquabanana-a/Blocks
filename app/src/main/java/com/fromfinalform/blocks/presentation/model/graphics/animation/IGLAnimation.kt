/*
 * Created by S.Dobranos on 12.02.21 19:50
 * Copyright (c) 2021. All rights reserved.
 */

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