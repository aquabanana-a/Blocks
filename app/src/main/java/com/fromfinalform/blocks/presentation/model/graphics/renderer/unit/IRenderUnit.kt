package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import com.fromfinalform.blocks.presentation.model.graphics.renderer.IRenderer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams

interface IRenderUnit : IRenderItem {

    fun prerender(renderer: IRenderer)
    fun render(renderer: IRenderer, renderParams: RenderParams, sceneParams: SceneParams)
    fun postrender(renderer: IRenderer)
}