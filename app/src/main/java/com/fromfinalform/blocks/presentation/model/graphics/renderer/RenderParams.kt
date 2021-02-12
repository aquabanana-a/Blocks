/*
 * Created by S.Dobranos on 11.02.21 20:51
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

data class RenderParams(
    val frame: Long,
    val timeMs: Long,
    val deltaTimeMs: Long) {
}