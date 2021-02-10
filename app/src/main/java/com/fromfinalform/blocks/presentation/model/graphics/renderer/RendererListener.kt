/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

interface RendererListener {
    fun onFrame(frame: Long, timeMs: Long, deltaTimeMs: Long)
    fun onStart()
    fun onFirstFrame()
    fun onStop()
    fun onCrash()

    fun onSceneConfigured(params: SceneParams)
}