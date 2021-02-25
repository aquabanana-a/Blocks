/*
 * Created by S.Dobranos on 05.02.21 20:33
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`.block

class BlockType(val id: BlockTypeId, val bgColor: Long, val separatorColor: Long, val txtColor: Long, val txtBgColor: Long = 0x00000000, val chanceCoeff: Int = 1) {
}