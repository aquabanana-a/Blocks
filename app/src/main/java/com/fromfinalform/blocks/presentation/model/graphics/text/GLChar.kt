package com.fromfinalform.blocks.presentation.model.graphics.text

import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLTextureRegion

class GLChar(val code: String, val isEmoji: Boolean = false) {
    companion object {
        const val CHAR_UNKNOWN = /*'?'*/32
        const val CHAR_BG = 0
    }

    constructor(code: Int, isEmoji: Boolean = false): this(code.toString(), isEmoji)
    constructor(code: Char, isEmoji: Boolean = false): this(code.toInt(), isEmoji)

    var width = 0.0f; private set // px
    var height = 0.0f; private set
    var fontId: Int? = null; private set // asset
    var fontSize: Float? = null; private set
    var textureRegion: GLTextureRegion? = null; private set

    fun isBackground() = code == CHAR_BG.toString()
    fun isUnknown() = code == CHAR_UNKNOWN.toString()

    // https://github.com/vdurmont/emoji-java
    fun getEmoji(): String {
        return if(isEmoji) code else CHAR_UNKNOWN.toString()
    }

    fun getGlyph(): Char {
        return (if(isEmoji) CHAR_UNKNOWN else Integer.parseInt(code)).toChar()
    }

    fun getSymbol(): String {
        return if(isEmoji) getEmoji() else getGlyph().toString()
    }

    fun setWidth(value: Float) {
        this.width = value
    }

    fun setHeight(value: Float) {
        this.height = value
    }

    fun setFontId(value: Int) {
        this.fontId = value
    }

    fun setFontSize(value: Float) {
        this.fontSize = value
    }

    fun setTextureRegion(value: GLTextureRegion) {
        this.textureRegion = value
    }
}