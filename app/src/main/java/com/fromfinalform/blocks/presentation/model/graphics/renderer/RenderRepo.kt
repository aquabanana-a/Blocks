package com.fromfinalform.blocks.presentation.model.graphics.renderer

import com.fromfinalform.blocks.presentation.model.graphics.drawer.IShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextTextureRepository
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextureRepository

data class RenderRepo(val shader: IShaderDrawerRepository, val texture: ITextureRepository, val textTexture: ITextTextureRepository) {
}