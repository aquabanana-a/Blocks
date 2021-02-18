/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import android.view.MotionEvent
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams

interface IGameField : IGameObjectsHolder {

    fun onTouch(me: MotionEvent, sp: SceneParams): Boolean

    fun init()

    fun highlightColumn(index: Int, value: Boolean = true)

//    fun canBePlaced(block: Block, column: Int): Boolean
//    fun placeTo(block: Block, column: Int)
//
//    fun clear()
}