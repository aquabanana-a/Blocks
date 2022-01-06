package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

import android.graphics.PointF
import android.graphics.RectF
import com.fromfinalform.blocks.common.clone

data class GLTextSheet(val rows: List<GLTextRow> = arrayListOf(), val glyphWidth: Float, val rowHeight: Float, val dst: RectF, private var clip: RectF? = null) {

    private var dstCurr: RectF? = null

    private var cleanGlyphCount: Int? = null
    private var totalGlyphCount: Int? = null
    private var cleanWordCount: Int? = null
    private var totalWordCount: Int? = null

    private var width: Float? = null
    private var height: Float? = null

    fun getCleanGlyphCount() = cleanGlyphCount!!
    fun getTotalGlyphCount() = totalGlyphCount!!

    fun getCleanWordCount() = cleanWordCount!!
    fun getTotalWordCount() = totalWordCount!!

    fun getRowCount() = rows.size

    fun getDstCurr() = dstCurr ?: dst

    fun getSize() = PointF(getWidth(), getHeight())
    fun getWidth() = width!!
    fun getHeight() = height!!

    fun recalculateMetrics(): GLTextSheet {
        rows.forEach { it.recalculateMetrics() }
        cleanGlyphCount = rows.sumBy { it.getCleanGlyphCount() }
        totalGlyphCount = rows.sumBy { it.getTotalGlyphCount() }
        cleanWordCount = rows.sumBy { it.getCleanWords().size }
        totalWordCount = rows.sumBy { it.words.size }
        width = rows.maxByOrNull { it.width }?.width ?: 0.0f
        height = rows.size * rowHeight
        return this
    }

    fun setCurrentDst(value: RectF): GLTextSheet {
        dstCurr = value.clone()
        return this
    }

    fun createClip(src: RectF = getDstCurr().clone()): RectF {
        clip = src
        return clip!!
    }

    fun getClipAndClear(): RectF? {
        var ret = clip?.clone()
        clip = null
        return ret
    }

    fun getMaxWidthRow(): GLTextRow = rows.maxByOrNull { it.width } ?: GLTextRow(arrayListOf(), 0.0f)
    operator fun get(index: Int) = rows[index]

    fun clone(): GLTextSheet {
        val ret = GLTextSheet(rows.map { it.clone() }, glyphWidth, rowHeight, dst.clone(), clip?.clone())
        ret.dstCurr = this.dstCurr
        ret.cleanGlyphCount = this.cleanGlyphCount
        ret.totalGlyphCount = this.totalGlyphCount
        ret.cleanWordCount = this.cleanWordCount
        ret.totalWordCount = this.totalWordCount
        ret.width = this.width
        ret.height = this.height
        return ret
    }
}