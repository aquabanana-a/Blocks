/*
 * Created by S.Dobranos on 08.02.21 1:26
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.data.repository

import com.fromfinalform.blocks.domain.model.block.BlockType
import com.fromfinalform.blocks.domain.model.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._2
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._4
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._8
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._16
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._32
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._64
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._128
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._256
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._512
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._1024
import com.fromfinalform.blocks.domain.model.block.BlockTypeId._2048
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository

class ClassicBlockTypeRepository : IBlockTypeRepository {

    private val blocksByTypeId = hashMapOf<BlockTypeId, BlockType>()
    override fun get(typeId: BlockTypeId): BlockType {
        return blocksByTypeId[typeId] ?: throw IllegalArgumentException()
    }

    private fun add(type: BlockType) {
        blocksByTypeId[type.id] = type
    }

    override fun initialize() {
        add(BlockType(_2, 0xFF757575, 0xFFFFFFFF))
        add(BlockType(_4, 0xFFE0B4B5, 0xFFFFFFFF))
        add(BlockType(_8, 0xFFB77775, 0xFFFFFFFF))
        add(BlockType(_16, 0xFF774965, 0xFFFFFFFF))
        add(BlockType(_32, 0xFFE8B077, 0xFFFFFFFF))
        add(BlockType(_64, 0xFF886C44, 0xFFFFFFFF))
        add(BlockType(_128, 0xFFC4B07B, 0xFFFFFFFF))
        add(BlockType(_256, 0xFF9F57AF, 0xFFFFFFFF))
        add(BlockType(_512, 0xFF7163CC, 0xFFFFFFFF))
        add(BlockType(_1024, 0xFF62AD4C, 0xFFFFFFFF))
        add(BlockType(_2048, 0xFF4995AA, 0xFFFFFFFF))
    }
}