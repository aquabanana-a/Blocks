package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

import android.graphics.RectF

data class GLTextWord(val glyphs: String, val width: Float, val height: Float?=null, val x: Float?=null, val y: Float?=null,val baseLine: Float? = null, val ascent: Float? = null, val descent: Float? = null) {

    // not current line, whole text (bundle of lines)
    private var paddings: RectF? = null
    val paddingStart get() = paddings?.left ?: 0f
    val paddingEnd get() = paddings?.right ?: 0f
    val paddingTop get() = paddings?.top ?: 0f
    val paddingBottom get() = paddings?.bottom ?: 0f

    fun withPaddings(value: RectF?): GLTextWord {
        this.paddings = value
        return this
    }

    private var cleanGlyphs: String? = null

    fun isClean() = !glyphs.contains(' ')

    fun getCleanGlyphs() = cleanGlyphs!!
    fun getCleanGlyphCount() = cleanGlyphs!!.length
    fun getTotalGlyphCount() = glyphs.length

    fun recalculateMetrics() {
        cleanGlyphs = if (isClean()) glyphs else glyphs.filter { it != ' ' }
    }

    operator fun get(index: Int) = glyphs[index]

    fun clone(): GLTextWord {
        val ret = GLTextWord(glyphs, width,height,x,y,baseLine, ascent, descent)
        ret.cleanGlyphs = this.cleanGlyphs
        return ret.withPaddings(paddings)
    }
}