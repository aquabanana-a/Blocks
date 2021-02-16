/*
 * Created by S.Dobranos on 12.02.21 21:23
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem

interface IGLCompletableAnimation : IGLAnimation {
    val isComplete: Boolean
    val completeHandler: ((renderItem: RenderItem, renderParams: RenderParams, sceneParams: SceneParams) -> Unit)?
}