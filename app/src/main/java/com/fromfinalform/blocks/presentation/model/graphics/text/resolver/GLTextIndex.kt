package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

data class GLTextIndex(var row: Int = 0, var word: Int = 0, var glyph: Int = 0, var glyphRow: Int = 0, var wordAbsolute: Int = 0, var glyphAbsolute: Int = 0) {
    fun rowInc() {
        row = row.inc()
    }

    fun wordInc() {
        word = word.inc()
    }

    fun glyphInc() {
        glyph = glyph.inc()
    }

    fun glyphRowInc() {
        glyphRow = glyphRow.inc()
    }

    fun wordAbsoluteInc() {
        wordAbsolute = wordAbsolute.inc()
    }

    fun glyphAbsoluteInc() {
        glyphAbsolute = glyphAbsolute.inc()
    }
    constructor(index: GLTextIndex):this(index.row,  index.word,index.glyph,index.glyphRow, index.wordAbsolute, index.glyphAbsolute)
    fun copy()= GLTextIndex(this)
    fun clone()= GLTextIndex(this)
}