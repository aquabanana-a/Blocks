/*
 * Created by S.Dobranos on 08.02.21 20:35
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle

data class GLTextStyle(var align: Int, var foregroundColor: Array<GLColor>, var backgroundColor: Array<GLColor>) {
    companion object {
        fun from(ts: TextStyle): GLTextStyle {
            val fc = GLColor(ts.textColor)
            val bc = GLColor(ts.textBackColor)
            return GLTextStyle(ts.textAlign, arrayOf(fc, fc.clone(), fc.clone(), fc.clone()), arrayOf(bc, bc.clone(), bc.clone(), bc.clone()))
        }
    }

    fun withColor(value: Long): GLTextStyle {
        val c = GLColor(value)
        foregroundColor.forEach { it.set(c) }
        return this
    }

    fun withBackColor(value: Long): GLTextStyle {
        val c = GLColor(value)
        backgroundColor.forEach { it.set(c) }
        return this
    }

    fun clone() = GLTextStyle(align, foregroundColor.clone(), backgroundColor.clone())

    fun Array<GLColor>.clone(): Array<GLColor> {
        return Array<GLColor>(this.size) { i -> this[i].clone() }
    }
}