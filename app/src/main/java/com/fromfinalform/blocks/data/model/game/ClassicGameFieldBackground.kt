/*
 * Created by S.Dobranos on 07.02.21 21:31
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.data.model.game

import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.model.game.IGameFieldBackground

class ClassicGameFieldBackground : IGameFieldBackground {

    override fun build(config: IGameConfig): GameObject {
        val ret = GameObject()
        ret.childs = arrayListOf()

        for (i in 0..config.fieldWidthBl) {
            val c = GameObject()
            c.x = i * config.blockWidthPx + (i + 1) * config.blockGapHPx
            c.width = config.blockWidthPx
            c.height = config.fieldHeightPx
            c.assetId = R.drawable.bg_03

            (ret.childs as MutableList).add(c)
        }

        return ret
    }
}