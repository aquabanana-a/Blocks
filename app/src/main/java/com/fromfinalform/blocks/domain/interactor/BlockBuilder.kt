/*
 * Created by S.Dobranos on 08.02.21 2:12
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.interactor

import android.view.Gravity
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

class BlockBuilder(val config: IGameConfig, val typeRepo: IBlockTypeRepository) {

    private var typeId = BlockTypeId._2

    fun withTypeId(typeId: BlockTypeId): BlockBuilder {
        this.typeId = typeId
        return this
    }

    fun withRandomTypeId(exclude: BlockTypeId?) = withRandomTypeId(if (exclude != null) arrayListOf(exclude) else null)
    fun withRandomTypeId(exclude: List<BlockTypeId>? = null): BlockBuilder {
        this.typeId = typeRepo.getRandom(exclude).id
        return this
    }

    fun build(): Block {
        val type = typeRepo[typeId]

        val ret = Block(typeId)
        ret.width = config.blockWidthPx
        ret.height = config.blockHeightPx
        ret.color = type.bgColor

        ret.requestAnimation(GameObject().apply {
            x = 0f//ret.width / 2
            y = ret.height / 2
            width = ret.width///2
            height = ret.height/2
            textStyle = TextStyle(typeId.toString(), 28f, R.font.jura_bold, type.txtColor, type.txtBgColor).withInnerGravity(Gravity.CENTER)
        })

        ret.requestAnimation(GameObject().apply {
            x = 0f
            y = ret.height / 2
            width = ret.width
            height = 1f
            color = type.separatorColor
        })

        return ret
    }
}