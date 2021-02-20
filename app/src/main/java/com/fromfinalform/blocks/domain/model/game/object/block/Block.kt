/*
 * Created by S.Dobranos on 08.02.21 1:53
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`.block

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.GameObjectIndexer

class Block(val typeId: BlockTypeId, id: Long = GameObjectIndexer.getNext()) : GameObject(id) {

    override fun clone(): Block {
        val ret = Block(typeId)
        ret.x = x
        ret.y = y
        ret.width = width
        ret.height = height
        ret.assetId = assetId
        ret.color = color
        ret.textStyle = textStyle

        if (childs != null) ret.childs = childs!!.map { it.clone() }
        if (animations != null) ret.animations = animations!!.map { it.clone() }

        return ret
    }
}