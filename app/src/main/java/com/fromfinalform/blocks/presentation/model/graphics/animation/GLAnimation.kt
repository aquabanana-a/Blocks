/*
 * Created by S.Dobranos on 10.02.21 23:42
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.animation

import android.view.animation.Interpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams

abstract class GLAnimation(
    val startTimeMs: Long,
    val upms: Float /*speed*/,
    val interpolator: Interpolator) {

    abstract fun transform(renderParams: RenderParams, sceneParams: SceneParams, itemParams: ItemParams)
}