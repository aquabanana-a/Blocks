/*
 * Created by S.Dobranos on 16.02.21 21:19
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.mode.classic

import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import javax.inject.Inject

class ClassicGameLooper : IGameLooper {

    @Inject constructor()

    @Inject lateinit var config: IGameConfig
    @Inject lateinit var field: IGameField

    override var nextBlock: Block? = null; private set

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}