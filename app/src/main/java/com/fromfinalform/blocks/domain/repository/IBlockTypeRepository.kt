/*
 * Created by S.Dobranos on 19.11.20 14:57
 * Copyright (c) 2020. All rights reserved.
 */

package com.fromfinalform.blocks.domain.repository

import com.fromfinalform.blocks.domain.model.block.BlockType
import com.fromfinalform.blocks.domain.model.block.BlockTypeId

interface IBlockTypeRepository {
    fun initialize()
    operator fun get(typeId: BlockTypeId): BlockType
}