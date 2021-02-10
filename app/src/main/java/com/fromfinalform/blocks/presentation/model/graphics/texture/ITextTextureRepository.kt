/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.texture

import com.fromfinalform.blocks.presentation.model.graphics.text.GLTextTexture
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

interface ITextTextureRepository {
    operator fun get(textStyle: TextStyle): GLTextTexture
}