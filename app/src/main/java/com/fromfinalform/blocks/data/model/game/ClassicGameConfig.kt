/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.data.model.game

import com.fromfinalform.blocks.domain.model.game.IGameConfig

class ClassicGameConfig : IGameConfig {

    override val blockWidthPx get() = 100f
    override val blockHeightPx get() = 100f
    override val blockGapHPx get() = 5f
    override val blockGapVPx get() = 5f

    override val fieldWidth get() = 5
    override val fieldHeight get() = 7

    override val fieldWidthPx get() = blockWidthPx * fieldWidth + blockGapHPx * (fieldWidth + 1)
    override val fieldHeightPx get() = blockHeightPx * fieldHeight + blockGapVPx * (fieldHeight + 1)

//    @Inject constructor() {
//
//    }
}