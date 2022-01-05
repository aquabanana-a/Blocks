package com.fromfinalform.blocks.domain.model.game.mode.classic

import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import javax.inject.Inject

class ClassicGameConfig : IGameConfig {

    @Inject constructor()

    override val blockWidthPx get() = 100f
    override val blockHeightPx get() = 100f
    override val blockGapHPx get() = 6f
    override val blockGapVPx get() = 6f

    override val blockCurrGapTopPx get() = 40f
    override val blockCurrGapBottomPx get() = 20f

    override val fieldWidthBl get() = 5
    override val fieldHeightBl get() = 7

    override val fieldWidthPx get() = blockWidthPx * fieldWidthBl + blockGapHPx * (fieldWidthBl - 1)
    override val fieldHeightPx get() = blockHeightPx * fieldHeightBl + blockGapVPx * (fieldHeightBl - 1)

    override val canvasWidthPx get() = fieldWidthPx
    override val canvasHeightPx get() = fieldHeightPx + blockCurrGapTopPx + blockGapVPx + blockGapVPx + blockHeightPx + blockGapVPx + blockCurrGapBottomPx

}