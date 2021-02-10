/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import com.fromfinalform.blocks.presentation.model.graphics.renderer.IRenderer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams

interface IRenderUnit {
    val id: Long
    val childs: List<RenderItem>?

    fun addChild(value: RenderItem)
    fun removeChild(id: Long): RenderItem?

    fun prerender(renderer: IRenderer)
    fun render(renderer: IRenderer, params: SceneParams, timeMs: Long, deltaTimeMs: Long)
    fun postrender(renderer: IRenderer)
}