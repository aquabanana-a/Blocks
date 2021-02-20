/*
 * Created by S.Dobranos on 20.02.21 19:39
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

import com.fromfinalform.blocks.presentation.model.graphics.drawer.IShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextTextureRepository
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextureRepository

data class RenderRepo(val shader: IShaderDrawerRepository, val texture: ITextureRepository, val textTexture: ITextTextureRepository) {
}