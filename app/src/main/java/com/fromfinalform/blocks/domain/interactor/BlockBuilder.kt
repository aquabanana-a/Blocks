/*
 * Created by S.Dobranos on 08.02.21 2:12
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.interactor

import com.fromfinalform.blocks.data.repository.ClassicBlockTypeRepository
import com.fromfinalform.blocks.domain.model.block.Block
import com.fromfinalform.blocks.domain.model.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.IGameConfig

class BlockBuilder(val config: IGameConfig) {

    private var blockTypeRepo = ClassicBlockTypeRepository().apply { initialize() }
    private var typeId = BlockTypeId._2

    fun withTypeId(typeId: BlockTypeId): BlockBuilder {
        this.typeId = typeId
        return this
    }

    fun build(): Block {
        val ret = Block(typeId)
        ret.width = config.blockWidthPx
        ret.height = config.blockHeightPx
        ret.color = blockTypeRepo[typeId].bgColor
        return ret
    }
}