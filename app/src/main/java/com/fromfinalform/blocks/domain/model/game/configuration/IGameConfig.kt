/*
 * Created by S.Dobranos on 20.02.21 13:39
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.configuration

interface IGameConfig {
    val blockWidthPx: Float // width/height in pixels
    val blockHeightPx: Float // width/height in pixels

    val blockGapHPx: Float
    val blockGapVPx: Float

    val fieldWidth: Int // in cells
    val fieldHeight: Int

    val fieldWidthPx: Float
    val fieldHeightPx: Float
}