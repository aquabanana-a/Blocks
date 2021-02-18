/*
 * Created by S.Dobranos on 16.02.21 21:15
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.mode.classic

import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import javax.inject.Inject

class ClassicGameConfig : IGameConfig {

    @Inject constructor()

    override val blockWidthPx get() = 100f
    override val blockHeightPx get() = 100f
    override val blockGapHPx get() = 6f
    override val blockGapVPx get() = 6f

    override val fieldWidth get() = 5
    override val fieldHeight get() = 7

    override val fieldWidthPx get() = blockWidthPx * fieldWidth + blockGapHPx * (fieldWidth - 1)
    override val fieldHeightPx get() = blockHeightPx * fieldHeight + blockGapVPx * (fieldHeight - 1)

}