package com.fromfinalform.blocks.presentation.model.graphics.text.resolver

import android.graphics.PointF
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import com.fromfinalform.blocks.common.clone
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.common.mul
import com.fromfinalform.blocks.common.toRectF
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ISpriteDrawer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams
import com.fromfinalform.blocks.presentation.model.graphics.text.GLChar
import com.fromfinalform.blocks.presentation.model.graphics.text.GLTextTexture
import com.fromfinalform.blocks.presentation.model.graphics.text.TextLayout
import kotlin.math.abs
import kotlin.math.floor

class GLTextResolver(val glTextTexture: GLTextTexture, val params: SceneParams) {

//    class GLTextTransformValue(val transformer: IGLTextTransformer, var transformMs: Float) {
//        var interpolatedValue = 1f
//    }

    companion object {
        const val CHR_WORD_SPLITTER = ' '
        const val CHR_NEWLINE = '\n'
        const val CHR_NEWLINE_MARK = '▄'

        const val STR_WORD_SPLITTER = CHR_WORD_SPLITTER.toString()
        const val STR_NEWLINE = CHR_NEWLINE.toString()
        const val STR_NEWLINE_MARK = CHR_NEWLINE_MARK.toString()
    }

    var preconfiguredSheet: GLTextSheet? = null

    private var drawer: ISpriteDrawer? = null
    private var textLayout: TextLayout? = null
    private var bgOffsetXY = /*PointF((-9).dp, (-2).dp)*/ PointF()

    private var transformersLo = Any()
//    private var transformers = arrayListOf<GLTextTransformValue>()

    init {
        if (glTextTexture.textStyle == null)
            throw IllegalArgumentException()

//        val ts = glTextTexture.textStyle!!.clone()
//        val dst = origDst.clone()
//
//        val scaleItemXY = PointF(abs(dst.width() / origDst.width()), abs(dst.heightInv() / origDst.heightInv()))
//        val scaleCanvasXY = PointF(2f / params.sceneWH.x, 2f / params.sceneWH.y)
//        val scaleXY = PointF(scaleCanvasXY.x * scaleItemXY.x, scaleCanvasXY.y * scaleItemXY.y)
//
//        val sizeWH = PointF(glTextTexture.cellWidth * scaleXY.x, glTextTexture.cellHeight * scaleXY.y)
//        val offsetXY = PointF(sizeWH.x / 2.0f - glTextTexture.fontPadX * scaleXY.x, sizeWH.y / 2.0f - glTextTexture.fontPadY * scaleXY.y)
//        val startXY = PointF(dst.left + offsetXY.x, dst.top - offsetXY.y)
//
//        var rowH = (glTextTexture.fontSpacing + glTextTexture.spaceY) * ts.lineSpaceMultiplier * scaleXY.y
//        preconfiguredSheet = GLTextSheet(splitOnRows(ts.text, templateItem, rowH, dst, startXY, offsetXY, scaleXY), sizeWH.x, rowH, dst.clone()).recalculateMetrics()
    }

    fun withTextLayout(value: TextLayout?): GLTextResolver {
        this.textLayout = value
        return this
    }

    fun withTextDrawer(value: ISpriteDrawer?): GLTextResolver {
        this.drawer = value
        return this
    }

    fun withBgOffset(valueXY: PointF): GLTextResolver {
        this.bgOffsetXY = valueXY
        return this
    }

//    fun addTransformer(transformer: IGLTextTransformer, ms: Float = 0f): GLTextResolver {
//        synchronized(transformersLo) {
//            var found = false
//            for (t in this.transformers)
//                if (t.transformer.equals(transformer)) {
//                    found = true
//                    t.transformMs = ms
//                    break
//                }
//
//            if (!found)
//                this.transformers.add(GLTextTransformValue(transformer, ms))
//            return this
//        }
//    }
//
//    fun clearTransformer() {
//        synchronized(transformersLo) {
//            this.transformers.clear()
//        }
//    }

//    private fun interpolateTransformImpl(transformValue: GLTextTransformValue, sheet: GLTextSheet) {
//        if (transformValue.transformMs < 0)
//            return
//
//        val startMs = transformValue.transformer.getTextStartMs()
//        var durationMs = getTransformDurationMs(transformValue, sheet)
//        var value = max(0.0, min((transformValue.transformMs - startMs) / durationMs.toDouble(), 1.0))
//
//        transformValue.interpolatedValue = (transformValue.transformer.getTextInterpolator() ?: IRenderUnit.defaultInterpolator).getInterpolation(value.toFloat())
//    }
//
//    private fun getTransformDurationMs(transformValue: GLTextTransformValue, sheet: GLTextSheet): Long {
//        val startMs = transformValue.transformer.getTextStartMs()
//        val durationMs = transformValue.transformer.getTextDurationMs()
//        return if (transformValue.transformer is IGLTextTimelineChanger) transformValue.transformer.changeTimeline(startMs, durationMs, sheet) else durationMs
//    }
//
//    fun getTransformMaxDurationMs(): Long {
//        synchronized(transformersLo) {
//            var ret = 0L
//            transformers.forEach { ret = max(ret, getTransformDurationMs(it, preconfiguredSheet)) }
//            return ret
//        }
//    }

    fun drawText(itemParams: ItemParams) {
        val dst = itemParams.dstRect
        val reconfigure = false

        val origDst = dst.clone() // TODO
        val textStyle = glTextTexture.textStyle!!

        val scaleItemXY = PointF(abs(dst.width() / origDst.width()), abs(dst.heightInv() / origDst.heightInv()))
        val scaleCanvasXY = PointF(2f / params.sceneWH.x, 2f / params.sceneWH.y)
        val scaleXY = PointF(scaleCanvasXY.x * scaleItemXY.x, scaleCanvasXY.y * scaleItemXY.y)

        val sx = scaleXY.x / params.scale
        val sy = scaleXY.y / params.scale
        val txtDst = if (textLayout != null) dst.clone()
                    else RectF(dst.left + textStyle.paddingLeft * sx, dst.top - textStyle.paddingTop * sy, dst.right - textStyle.paddingRight * sx, dst.bottom + textStyle.paddingBottom * sy)

        val sizeWH = PointF(glTextTexture.cellWidth * scaleXY.x, glTextTexture.cellHeight * scaleXY.y)
        val offsetXY = PointF(sizeWH.x / 2.0f - glTextTexture.fontPadX * scaleXY.x, sizeWH.y / 2.0f - glTextTexture.fontPadY * scaleXY.y)
        val startXY = PointF(txtDst.left + offsetXY.x, txtDst.top - offsetXY.y)



        // only for forced
//        startXY.x += textStyle.paddingLeft * sx
//        startXY.y -= textStyle.paddingTop * sy

        if (preconfiguredSheet == null || preconfiguredSheet!!.rows.isEmpty() || reconfigure) {
            var rowH = (glTextTexture.fontSpacing + glTextTexture.spaceY) * textStyle.lineSpaceMultiplier * scaleXY.y
            preconfiguredSheet = GLTextSheet(splitOnRows(textLayout, rowH, txtDst, startXY, offsetXY, scaleXY), sizeWH.x, rowH, txtDst.clone()).recalculateMetrics()
        }
        preconfiguredSheet!!.setCurrentDst(txtDst)

        val sheet = preconfiguredSheet!!.clone()
        val style = GLTextStyle.from(textStyle)
        val index = GLTextIndex()

//        synchronized(transformersLo) {
//            transformers.forEach { interpolateTransformImpl(it, sheet) }
//        }

        val bgDst = RectF(dst.left + bgOffsetXY.x * sx, dst.top - bgOffsetXY.y * sy, dst.right - bgOffsetXY.x * sx, dst.bottom + bgOffsetXY.y * sy)
//        drawTextBackground(bgDst, style, sheet)

        drawTextBackground(/*bgDst*/txtDst, style, sheet)
//
//        val d = txtDst.clone()
//        d.top = txtDst.top - preconfiguredSheet!!.rowHeight
//        d.bottom = d.top - preconfiguredSheet!!.rowHeight
//        drawTextBackground(d, style.withBackColor(0xFF0000FF), sheet)
//
//        val d2 = d.clone()
//        d2.top = d.top - preconfiguredSheet!!.rowHeight
//        d2.bottom = d2.top - preconfiguredSheet!!.rowHeight
//        drawTextBackground(d2, style.withBackColor(0xFF00FF00), sheet)

        var textXY = PointF(startXY.x, startXY.y)

        // todo
        val preconfiguredWidthPerc = 1f//preconfiguredSheet!!.dst.width() / origDst.width()
        val preconfiguredHeightPerc = 1f//preconfiguredSheet!!.dst.heightInv() / origDst.heightInv()
        val currentWidthPerc = dst.width() / origDst.width()
        val currentHeightPerc = dst.heightInv() / origDst.heightInv()
        val currentScaleXY = PointF(currentWidthPerc / preconfiguredWidthPerc, currentHeightPerc / preconfiguredHeightPerc)

        // text sheet size calculated on sheet-creation stage
        val preconfiguredSizeWH = sheet.getSize()

        // we need to recalculate them to actual values without recreation sheet
        val sheetWidth = preconfiguredSizeWH.x * currentScaleXY.x
        val sheetHeight = preconfiguredSizeWH.y * currentScaleXY.y

        val innerGravity = textStyle.innerGravity
        val hg = innerGravity and (Gravity.AXIS_PULL_BEFORE or Gravity.AXIS_PULL_AFTER)
        val haveLeftGravity = ((hg and Gravity.AXIS_PULL_BEFORE) != 0)
        val haveRightGravity = ((hg and Gravity.AXIS_PULL_AFTER) != 0)
        val vg = (innerGravity shr Gravity.AXIS_Y_SHIFT) and (Gravity.AXIS_PULL_BEFORE or Gravity.AXIS_PULL_AFTER)
        val haveTopGravity = (vg and Gravity.AXIS_PULL_BEFORE) != 0
        val haveBottomGravity = (vg and Gravity.AXIS_PULL_AFTER) != 0

        if (!(haveTopGravity xor haveBottomGravity))
            textXY.y -= (txtDst.heightInv() - sheetHeight) * .5f
        else if (haveBottomGravity)
            textXY.y -= (txtDst.heightInv() - sheetHeight)

        if (!(haveLeftGravity xor haveRightGravity))
            startXY.x += (txtDst.width() - sheetWidth) * .5f
        else if (haveRightGravity)
            startXY.x += (txtDst.width() - sheetWidth)

        val heightSheetCurrent = (sheet.rows.size * preconfiguredSheet!!.rowHeight) * currentScaleXY.y

        for (i in sheet.rows.indices) {
            var r = sheet.rows[i]

            var alignOffsetX = 0.0f
            //var alignOffsetY = (dst.heightInv() - heightSheetCurrent) * .5f - (1f - glTextTexture.textStyle!!.lineSpaceMultiplier) * preconfiguredSheet!!.rowHeight / glTextTexture.textStyle!!.lineSpaceMultiplier// v gravity center

            var widthRowCurrent = r.width * currentScaleXY.x

            when (style.align) {
                View.TEXT_ALIGNMENT_TEXT_START -> alignOffsetX += 0f
                View.TEXT_ALIGNMENT_TEXT_END -> alignOffsetX += (sheetWidth - widthRowCurrent)
                View.TEXT_ALIGNMENT_CENTER -> alignOffsetX += (sheetWidth - widthRowCurrent) * 0.5f
            }

            textXY.x = startXY.x + alignOffsetX

            var h = if (r.height != null)
                r.height!! * currentScaleXY.y
            else
                preconfiguredSheet!!.rowHeight * currentScaleXY.y

            index.word = 0
            index.glyphRow = 0
            for (w in r.words.indices) {
                val glTextWord = r.words[w]
                val chars = glTextWord.glyphs

                if (glTextWord.baseLine != null) {
                    alignOffsetX = 0f
                    when (style.align) {
                        View.TEXT_ALIGNMENT_TEXT_START -> alignOffsetX += glTextWord.paddingStart / params.scale * currentScaleXY.x
                        View.TEXT_ALIGNMENT_TEXT_END -> alignOffsetX += dst.width() - (widthRowCurrent + glTextWord.paddingEnd / params.scale * currentScaleXY.x)
                        View.TEXT_ALIGNMENT_CENTER -> alignOffsetX += (dst.width() - widthRowCurrent) * 0.5f
                    }

                    var midPointH = (glTextTexture.fontAscent + glTextTexture.fontDescent) * 0.5f
                    var baseLineOffsetH = (glTextTexture.fontAscent - midPointH) * scaleXY.y

                    var x = dst.left + glTextWord.x!! * currentScaleXY.x + offsetXY.x + alignOffsetX
                    var y = dst.top - ((glTextWord.baseLine!! + glTextWord.paddingTop) / params.scale - baseLineOffsetH) * currentScaleXY.y

                    // used in template canvas
                    drawTextWord(
                        PointF(x, y), sizeWH, scaleXY, chars, style, sheet, if (chars != STR_WORD_SPLITTER) index else null
                    )
                } else { // old way used only in TextEditFragment // TODO: convert to single variant
                    drawTextWord(
                        textXY, // !Note: coord of center symbol. Not TL corner!
                        sizeWH,
                        scaleXY,
                        chars,
                        style,
                        sheet,
                        if (chars != STR_WORD_SPLITTER) index else null
                    )
                }

                index.wordInc()
            }

            textXY.y -= h
            index.rowInc()
        }
    }

    private fun drawTextBackground(dst: RectF, ts: GLTextStyle, sheet: GLTextSheet) {
        synchronized(transformersLo) {
            val bgCh = glTextTexture.getGLChar(GLTextTexture.CHAR_BG)
            val bgWH = PointF(dst.width(), dst.height())
            val bgXY = PointF(dst.left + bgWH.x / 2f, dst.top + bgWH.y / 2f)

//            transformers.forEach {
//                val t = it.transformer
//                val v = it.interpolatedValue
//                t.transform(bgCh, bgXY, bgWH, ts, sheet, null, v)
//            }
            drawer?.drawSprite(bgXY.x, bgXY.y, bgWH.x, bgWH.y, bgCh.textureRegion!!, ts.backgroundColor, sheet.getClipAndClear(), endBatch = true)
        }
    }

    private fun drawTextWord(textXY: PointF, sizeWH: PointF, scaleXY: PointF, word: String, ts: GLTextStyle, sheet: GLTextSheet, index: GLTextIndex? = null) {
        index?.glyph = 0
        splitWordOnSymbols(word).forEach {
            val glc = glTextTexture.getGLChar(it)

            drawTextChar(textXY.clone(), sizeWH.clone(), glc, ts, sheet, index)
            if (index != null) {
                index.glyphInc()
                index.glyphRowInc()
                index.glyphAbsoluteInc()
            }
            textXY.x += calcCharStepW(glc, glTextTexture, scaleXY)
        }
        index?.wordAbsoluteInc()
    }

    // we need this because Emoji can consists of 2+ chars
    private fun splitWordOnSymbols(word: String): ArrayList<String> {
        val ret = arrayListOf<String>()
        val haveEmoji = false//EmojiManager.containsEmoji(word)

        if (!haveEmoji)
            word.forEach { ret.add(it.toString()) }
        else {
            var processingWord = word
            val emojis = hashMapOf<String, String>()
            var emojiIndex = 0
            val separator = "▄"
            val uniquelizer = "˧"

//            EmojiParser.extractEmojis(word).forEach {
//                val key = "${uniquelizer}${emojiIndex}"
//                val replacer = "${separator}${key}${separator}"
//                emojis[key] = it
//                processingWord = processingWord.replaceFirst(it, replacer)
//                emojiIndex++
//            }

            processingWord.split(separator).forEach {
                val e = emojis[it]
                if (e != null)
                    ret.add(e)
                else
                    it.forEach { ret.add(it.toString()) }
            }
        }
        return ret
    }

    private fun drawTextChar(textXY: PointF, sizeWH: PointF, char: GLChar, ts: GLTextStyle, sheet: GLTextSheet, index: GLTextIndex? = null) {
        synchronized(transformersLo) {
//            val additionalCharsForDraw = mutableListOf<CharDrawParams>()
//            transformers.forEach {
//                val t = it.transformer
//                val v = it.interpolatedValue
//                t.transform(char, textXY, sizeWH, ts, sheet, index, v, additionalCharsForDraw)
//            }
            drawer?.drawSprite(textXY.x, textXY.y, sizeWH.x, sizeWH.y, char.textureRegion!!, ts.foregroundColor, sheet.getClipAndClear(), useTextureColors = char.isEmoji)
//            additionalCharsForDraw.forEach {
//                if (it.preRenderPredicate?.invoke() ?: true) {
//                    var c = it.char?.let { glText.getGLChar(it) } ?: char
//                    drawer?.drawSprite(it.x, it.y, it.width, it.height, c.textureRegion ?: char.textureRegion!!, it.colors, it.clip, endBatch = it.endBatch, useTextureColors = it.useTextureColors ?: c.isEmoji)
//                }
//            }
        }
    }

    private fun calcCharStepW(glc: GLChar, glt: GLTextTexture, scaleXY: PointF): Float {
        return (glc.width + glt.spaceX) * scaleXY.x
    }

    private fun splitOnWords(text: String): List<String> {
        return text.split(Regex("((?<=$CHR_WORD_SPLITTER)|(?=$CHR_WORD_SPLITTER))"))
    }

    private fun splitOnRowsPrepared(textLayout: TextLayout, scaleXY: PointF): List<GLTextRow> {
        val rows = ArrayList<GLTextRow>()

        textLayout.textLines.forEach {
            val text = it.lineText
            val words = splitOnWords(text)

            var rowW = 0.0f
            var row = ArrayList<GLTextWord>()

            for (wv in words.indices) {
                var word = words[wv]
                var wordW = getWordW(word, scaleXY)

                if (wordW > 0) {
                    row.add(
                        GLTextWord(
                            word, wordW,
                            it.bounds.height() * scaleXY.y,
                            it.bounds.left * scaleXY.x + rowW,
                            it.bounds.top * scaleXY.y,
                            it.baseLine * scaleXY.y,
                            it.ascent * scaleXY.y,
                            it.descent * scaleXY.y
                        )
                            .withPaddings(it.paddings.toRectF().mul(scaleXY))
                    )
                    rowW += wordW
                }
            }

            rows.add(GLTextRow(row, rowW, it.bounds.height() * scaleXY.y, it.bounds.left * scaleXY.x, it.bounds.top * scaleXY.y, it.baseLine * scaleXY.y))
        }
        return rows
    }

    private fun splitOnRowsForced(rowH: Float, dst: RectF, startXY: PointF, offsetXY: PointF, scaleXY: PointF): List<GLTextRow> {
        val ts = glTextTexture.textStyle!!
        val text = ts.text
        val words = splitOnWords(text)
        val rows = ArrayList<GLTextRow>()
        var row = ArrayList<GLTextWord>()

        val dh = dst.heightInv()
        val rowsMaxCount = floor(dst.heightInv() / rowH)
        val rowMaxW = dst.width().toDouble()
        var rowW = 0.0f

        for (w in words.indices) {
            val wordIn = if (words[w].isEmpty()) STR_WORD_SPLITTER else words[w]
            val wordsValid = splitOnWords(wordIn.replace(STR_NEWLINE, "$CHR_NEWLINE$CHR_NEWLINE_MARK").split(CHR_NEWLINE), CHR_NEWLINE_MARK, rowMaxW, scaleXY)

            for (wv in wordsValid.indices) {
                var word = wordsValid[wv]
                var wordW = getWordW(word, scaleXY)

                if (word != STR_NEWLINE_MARK && (rowW + wordW) <= rowMaxW) {
                    if (wordW > 0) {
                        if (row.size <= 0 && word == STR_WORD_SPLITTER) {
                        } else {
                            row.add(GLTextWord(word, wordW))
                            rowW += wordW
                        }
                    }
                } else {
                    if ((row.size > 0 || row.size == 0 && word == STR_NEWLINE_MARK) && (rows.size + 1) <= rowsMaxCount)
                        rows.add(GLTextRow(row, rowW))
                    rowW = 0.0f
                    row = ArrayList()
                    if (wordW > 0 && word != STR_NEWLINE_MARK) {
                        if (row.size <= 0 && word == STR_WORD_SPLITTER) {
                        } else {
                            row.add(GLTextWord(word, wordW))
                            rowW += wordW
                        }
                    }
                }
            }
        }

        // (startXY.y + offsetXY.y - rowH * rows.size) >= (dst.bottom + ts.paddingBottom * scaleXY.y / params.scale)
        if (row.size > 0 && (rows.size + 1) <= rowsMaxCount)
            rows.add(GLTextRow(row, rowW))

        return rows
    }

    private fun splitOnRows(textLayout: TextLayout?, rowH: Float, dst: RectF, startXY: PointF, offsetXY: PointF, scaleXY: PointF): List<GLTextRow> {
        if (textLayout != null && textLayout.textLines.size > 0)
            return splitOnRowsPrepared(textLayout, scaleXY)
        else
            return splitOnRowsForced(rowH, dst, startXY, offsetXY, scaleXY)
    }

    private fun splitOnWords(words: List<String>, newLineCh: Char, maxW: Double, scaleXY: PointF): List<String> {
        val ret = ArrayList<String>()

        val wordsNL = ArrayList<String>()
        for (w in words.indices)
            if (words[w].startsWith(newLineCh)) {
                wordsNL.add(words[w].substring(0, 1))
                if (words[w].length > 1)
                    wordsNL.add(words[w].substring(1))
            } else
                wordsNL.add(words[w])

        for (w in wordsNL.indices)
            if (getWordW(wordsNL[w], scaleXY) <= maxW)
                ret.add(wordsNL[w])
            else
                ret.addAll(splitWord(wordsNL[w], maxW, scaleXY))
        return ret
    }

    private fun splitWord(word: String, maxW: Double, scaleXY: PointF): List<String> {
        val ret = ArrayList<String>()

        var w = ArrayList<Char>()
        var wW = 0.0f
        splitWordOnSymbols(word).forEach {
            val gcw = calcCharStepW(glTextTexture.getGLChar(it), glTextTexture, scaleXY)
            if (wW + gcw <= maxW) {
                w.addAll(it.toList())
                wW += gcw
            } else {
                ret.add(String(w.toCharArray()))
                w = arrayListOf()
                w.addAll(it.toList())
                wW = gcw
            }
        }
        if (w.size > 0) ret.add(String(w.toCharArray()))
        return ret
    }

    private fun getWordW(word: String, scaleXY: PointF): Float {
        var wordW = 0.0f
        splitWordOnSymbols(word).forEach {
            val gc = glTextTexture.getGLChar(it)
            wordW += calcCharStepW(gc, glTextTexture, scaleXY)
        }
        return wordW
    }
}