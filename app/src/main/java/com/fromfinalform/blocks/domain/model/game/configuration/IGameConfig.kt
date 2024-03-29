package com.fromfinalform.blocks.domain.model.game.configuration

interface IGameConfig {
    val blockWidthPx: Float // width/height in pixels
    val blockHeightPx: Float // width/height in pixels

    val blockGapHPx: Float
    val blockGapVPx: Float

    val blockCurrGapTopPx: Float
    val blockCurrGapBottomPx: Float

    val fieldWidthBl: Int // in cells
    val fieldHeightBl: Int

    val fieldWidthPx: Float
    val fieldHeightPx: Float

    val canvasWidthPx: Float
    val canvasHeightPx: Float
}