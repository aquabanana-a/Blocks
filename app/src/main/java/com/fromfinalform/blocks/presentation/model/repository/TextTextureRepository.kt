/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.repository

import android.content.Context
import com.fromfinalform.blocks.presentation.model.graphics.text.GLTextTexture
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextTextureRepository

class TextTextureRepository(val context: Context) : ITextTextureRepository {

    private val texturesByStyle = hashMapOf<TextStyle, GLTextTexture>()
    private val texturesLo = Any()

    override fun get(textStyle: TextStyle): GLTextTexture { synchronized(texturesLo) {
        var texture = texturesByStyle[textStyle]
        if (texture == null) {
            texture = GLTextTexture(textStyle)
            texture.load(context)
            texturesByStyle[textStyle] = texture
        }

        return texture
    } }

}