/*
 * Created by S.Dobranos on 19.11.20 14:57
 * Copyright (c) 2020. All rights reserved.
 */

package com.fromfinalform.blocks.domain.repository

import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId

interface IBlockTypeRepository {
    fun initialize()
    operator fun get(typeId: BlockTypeId): BlockType
}