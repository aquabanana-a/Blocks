/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

import android.opengl.GLSurfaceView
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import kotlinx.coroutines.CoroutineScope

interface IRenderer : GLSurfaceView.Renderer {
    val scope: CoroutineScope

    val sceneSize: ISize
    val renderTimeMs: Long

    val renderUnits: List<IRenderUnit>
    fun add(ru: IRenderUnit)
    fun add(ru: List<IRenderUnit>)
    fun getRenderUnit(id: Long): IRenderUnit?
    fun removeRenderUnit(id: Long): IRenderUnit?
    fun clearRenderUnits()

    fun start()
    fun stop()
    fun requestRender()

    fun withListener(handler: RendererListener): IRenderer
    fun withUpdater(handler: () -> Unit): IRenderer
}