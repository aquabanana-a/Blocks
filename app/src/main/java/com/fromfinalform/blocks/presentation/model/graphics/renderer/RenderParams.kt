package com.fromfinalform.blocks.presentation.model.graphics.renderer

data class RenderParams(
    val frame: Long,
    val timeMs: Long,
    val deltaTimeMs: Long,
    val repos: RenderRepo
) {
}