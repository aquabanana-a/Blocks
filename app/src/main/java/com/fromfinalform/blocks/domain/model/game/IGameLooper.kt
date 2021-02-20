/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import android.view.MotionEvent
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams

interface IGameLooper : IGameObjectsHolder {

    val objectsDirtyFlat: List<GameObject>

    val nextBlock: Block?

    fun onTouch(me: MotionEvent, sp: SceneParams): Boolean

    fun init()
    fun start()
    fun stop()

    fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams)

    fun withObjectsCountChangedListener(handler: ((added: List<GameObject>?, removed: List<GameObject>?) -> Unit)?): IGameLooper
}