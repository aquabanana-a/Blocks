package com.fromfinalform.blocks.presentation.model.graphics.texture

import com.fromfinalform.blocks.presentation.model.graphics.text.GLTextTexture
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

interface ITextTextureRepository {
    operator fun get(textStyle: TextStyle): GLTextTexture
}