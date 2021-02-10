/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

import android.opengl.GLSurfaceView
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit

interface IRenderer : GLSurfaceView.Renderer {
    val sceneSize: ISize

    val renderUnits: List<IRenderUnit>
    fun add(ru: IRenderUnit)
    fun getRenderUnit(id: Long): IRenderUnit?
    fun removeRenderUnit(id: Long)
    fun clearRenderUnits()

    fun start()
    fun stop()
    fun requestRender()

    fun withListener(handler: RendererListener): IRenderer
    fun withUpdater(handler: () -> Unit): IRenderer
}