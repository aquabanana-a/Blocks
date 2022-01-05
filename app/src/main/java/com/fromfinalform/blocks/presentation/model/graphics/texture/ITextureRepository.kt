package com.fromfinalform.blocks.presentation.model.graphics.texture

interface ITextureRepository {
    operator fun get(assetId: Int): Int
    operator fun get(path: String): Int
}