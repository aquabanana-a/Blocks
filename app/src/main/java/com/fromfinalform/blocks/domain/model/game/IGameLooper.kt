/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig

interface IGameLooper {

    val nextBlock: Block?

    fun init()
    fun start()
    fun stop()
}