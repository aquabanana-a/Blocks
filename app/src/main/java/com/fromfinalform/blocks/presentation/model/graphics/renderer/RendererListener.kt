package com.fromfinalform.blocks.presentation.model.graphics.renderer

interface RendererListener {
    fun onFrame(renderParams: RenderParams, sceneParams: SceneParams)
    fun onStart()
    fun onFirstFrame()
    fun onStop()
    fun onCrash()

    fun onSceneConfigured(renderRepo: RenderRepo, params: SceneParams)
}