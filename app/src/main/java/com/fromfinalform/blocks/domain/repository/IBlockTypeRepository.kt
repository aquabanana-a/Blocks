package com.fromfinalform.blocks.domain.repository

import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId

interface IBlockTypeRepository {
    fun initialize()
    operator fun get(typeId: BlockTypeId): BlockType

    fun getRandom(exclude: List<BlockTypeId>? = null): BlockType
}