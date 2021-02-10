/*
 * Created by S.Dobranos on 08.02.21 20:35
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

data class GLTextRow(val words: List<GLTextWord>, val width: Float, val height: Float? = null, val x: Float?=null, val y: Float?=null, val baseLine: Float?=null,
                     val ascent: Float? = null,
                     val descent: Float? = null) {

    private var cleanWords: List<GLTextWord>? = null
    private var cleanGlyphCount: Int? = null
    private var totalGlyphCount: Int? = null

    fun getCleanWords() = cleanWords!!
    fun getCleanWordsCount() = cleanWords!!.size
    fun getCleanGlyphCount() = cleanGlyphCount!!
    fun getTotalGlyphCount() = totalGlyphCount!!

    fun recalculateMetrics() {
        words.forEach { it.recalculateMetrics() }
        cleanWords = words.filter { it.isClean() }
        cleanGlyphCount = words.sumBy { it.getCleanGlyphCount() }
        totalGlyphCount = words.sumBy { it.getTotalGlyphCount() }
    }

    operator fun get(index: Int) = words.getOrNull(index)

    fun clone(): GLTextRow {
        val ret = GLTextRow(words.map { it.clone() }, width,height,x,y,baseLine, ascent, descent)
        ret.cleanWords = this.cleanWords?.map { it.clone() }
        ret.cleanGlyphCount = this.cleanGlyphCount
        ret.totalGlyphCount = this.totalGlyphCount
        return ret
    }
}