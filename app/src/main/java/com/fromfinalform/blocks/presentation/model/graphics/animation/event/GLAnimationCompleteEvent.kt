package com.fromfinalform.blocks.presentation.model.graphics.animation.event

import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLCompletableAnimation
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem

class GLAnimationCompleteEvent(val src: IGLCompletableAnimation, val args: GLAnimationCompleteArgs)

class GLAnimationCompleteArgs(val renderItem: RenderItem, val renderParams: RenderParams, val sceneParams: SceneParams)