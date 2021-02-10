package com.fromfinalform.blocks.presentation.model.graphics.text

import android.content.Context
import android.graphics.*
import android.opengl.GLES20
import androidx.core.content.res.ResourcesCompat
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLTextureRegion
import com.vdurmont.emoji.EmojiParser
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

class GLTextTexture {
    companion object {
        const val CHAR_DEFAULT_START = 32
        const val CHAR_DEFAULT_END = 126
        const val CHAR_UNKNOWN = /*'?'*/32
        const val CHAR_BG = 0
    }

    var fontPadX = 0f       // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)
    var fontPadY = 0f

    var fontHeight = 0f    // Font Height (Actual; Pixels)
    var fontHeightOld = 0.0f
    var fontTop = 0f
    var fontBottom = 0f
    var fontSpacing = 0f
    var fontLeading = 0f
    var fontBaselineOffset = 0f
    var fontAscent = 0f    // Font Ascent (Above Baseline; Pixels)
    var fontAscentOffset = 0f
    var fontDescent = 0f    // Font Descent (Below Baseline; Pixels)
    var fontDescentOffset = 0f

    var textureId = -1; private set
    var textureSize = 0
    var textureRegion: GLTextureRegion? = null

    var charWidthMax = 0.0f
    var charHeightMax = 0.0f

    var cellWidth = 0f   // Character Cell Width/Height
    var cellHeight = 0f

    var rowCnt = 0      // Number of Rows/Columns
    var colCnt = 0

    var spaceX = 0f     // Additional (X,Y Axis) Spacing (Unscaled)
    var spaceY = 0f

    var textStyle: TextStyle? = null; private set

    private var charMap = HashMap<String, GLChar>()
    private var charUnknown: GLChar = GLChar(CHAR_UNKNOWN)
    private var charBg: GLChar = GLChar(CHAR_BG)

    private var charMapStr = "" // debug info only

    fun getGLChar(symbol: Int) = getGLChar(symbol.toChar())
    fun getGLChar(symbol: Char) = getGLChar(symbol.toString())
    fun getGLChar(symbol: String) = charMap[symbol] ?: if (symbol.isNotEmpty() && symbol[0].toInt() == CHAR_BG) charBg else charUnknown
    fun getGLCharsMapSize() = charMap.size + 1/*charUnknown*/ + 1/*charBg*/

    constructor(charMap: String? = null, charUnknown: Char = CHAR_UNKNOWN.toChar()) {
        this.charUnknown = GLChar(charUnknown.toInt())

        charMapStr = charMap ?: ""

        var cm = EmojiParser.removeAllEmojis(charMap ?: "")
        var em = EmojiParser.extractEmojis(charMap ?: "")

        if (cm.isNotEmpty()) (cm + "0123456789_").toSet().forEach {
            this.charMap[it.toString()] = GLChar(it.toInt())
        }
        else for (c in CHAR_DEFAULT_START..CHAR_DEFAULT_END) {
            this.charMap[c.toChar().toString()] = GLChar(c)
        }

        if (em.isNotEmpty()) em.toSet().forEach {
            this.charMap[it] = GLChar(it, true)
        }
    }

    constructor(ts: TextStyle) : this(ts.text) {
        textStyle = ts
    }

    fun isNeedToReload(ts: TextStyle): Boolean {
        return textStyle == null || textStyle!!.text != ts.text
                || textStyle!!.textSize != ts.textSize
                || textStyle!!.textFontId != ts.textFontId
    }

    fun isSame(value: GLTextTexture?): Boolean {
        return if (value == null) false
        else value.textStyle?.equals(value.textStyle)
            ?: false
    }

    fun updateStyle(ts: TextStyle) {
        textStyle!!.textAlign = ts.textAlign
        textStyle!!.textColor = ts.textColor
        textStyle!!.textBackColor = ts.textBackColor
    }

    fun load(context: Context): Int {
        if (textStyle == null)
            throw IllegalArgumentException()

        return load(context, textStyle!!.textFontId, textStyle!!.textSize)
    }

    fun load(context: Context, fontAssetId: Int, size: Float, padX: Int = (size / 2f).toInt(), padY: Int = (size / 2f).toInt()): Int {
        fontPadX = padX.toFloat()
        fontPadY = padY.toFloat()

        val tf = ResourcesCompat.getFont(context, fontAssetId)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = size
        paint.color = -0x1 // Set ARGB (White, Opaque)
        //paint.typeface = tf
        paint.typeface = Typeface.create(tf, Typeface.NORMAL) // to make non-italic emoji's

        // get font metrics
        val fm: Paint.FontMetrics = paint.fontMetrics
        fontTop = abs(fm.top)
        fontBottom = abs(fm.bottom)
        fontHeight = abs(fm.bottom) + abs(fm.top)
        fontHeightOld = ceil(abs(fm.ascent) + abs(fm.descent)/* + abs(fm.leading)*/)
        fontAscent = abs(fm.ascent)
        fontAscentOffset = (fontTop - fontAscent) / 2
        fontDescent = abs(fm.descent)
        fontDescentOffset = (fontBottom - fontDescent) / 2
        fontLeading = abs(fm.leading)
        fontSpacing = paint.fontSpacing

        //fontBaselineOffset = if(fontAscent == fontDescent) 0f else (fontSpacing - fontAscent)

        val charMapSorted = (charMap.values.sortedBy { it.getSymbol() } + charUnknown + charBg)

        // determine the width of each character (including unknown character)
        // also determine the maximum character width
        charWidthMax = 0.0f
        charHeightMax = fontHeightOld
        charMapSorted.forEach {
            if (it.code != CHAR_BG.toString()) {
                val symbol = it.getSymbol()
                val arr = FloatArray(symbol.length)

                paint.getTextWidths(symbol.toCharArray(), 0, symbol.length, arr)
                val w = arr.sum()

                charWidthMax = max(charWidthMax, w)
                it.setWidth(w)
            } else
                it.setWidth(charWidthMax)

            it.setHeight(charHeightMax)
            it.setFontId(fontAssetId)
            it.setFontSize(size)
        }

        cellWidth = charWidthMax.toInt() + 2 * fontPadX
        cellHeight = charHeightMax.toInt() + 2 * fontPadY
        val cellMaxWH = if (cellWidth > cellHeight) cellWidth else cellHeight

//        Log.d(
//            "my", """--------
//+Font:${fontAssetId}
//+top:${fontTop}
//+bottom:${fontBottom}
//+height:${fontHeight}
//+height_old:${fontHeightOld}
//+ascent:${fontAscent}
//+descent:${fontDescent}
//+leading:${fontLeading}
//+spacing:${fontSpacing}
//+        --------""".trimMargin()
//        )

        val cellS = cellWidth * cellHeight
        val textureS = getGLCharsMapSize() * cellS
        textureSize = ceil(sqrt(textureS) + cellMaxWH).toInt()

        val textureMaxSize = IntArray(1) // 16384
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, textureMaxSize, 0)
        if (textureSize > textureMaxSize[0])
            return -1

        var bitmap: Bitmap? = null
        try {
            bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888/*ALPHA_8*/)
        } catch (e: Exception) {
            throw RuntimeException("Create bitmap err. textureSize:${textureSize} charMapSize:${getGLCharsMapSize()} exception:${e}")
        }

        var canvas = Canvas(bitmap) as Canvas?
        bitmap!!.eraseColor(/*0xFF008100*/0x00000000.toInt())

        colCnt = (textureSize / cellWidth).toInt()
        rowCnt = ceil(getGLCharsMapSize().toFloat() / colCnt.toFloat()).toInt()

        var x: Float = fontPadX
        var y: Float = cellHeight - fontDescent - fontPadY
        charMapSorted.forEach {
            if (it.code != CHAR_BG.toString()) {
                var txt = it.getSymbol()
                canvas!!.drawText(txt, 0, txt.length, x, y, paint)
            } else
                canvas!!.drawRect(RectF(x - fontPadX, y - it.height + fontDescent - fontPadY, x + it.width + fontPadX, y + fontDescent + fontPadY), paint)
            x += cellWidth
            if (x + cellWidth - fontPadX > textureSize) {
                x = fontPadX
                y += cellHeight
            }
        }

        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        textureId = textureIds[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        //GLUtils.checkGlError("glBindTexture mTextureID")
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLUtils.checkGlError("before load GLText texture")
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        GLUtils.checkGlError("on load GLText texture " + textureSize + "x" + textureSize + "(GL_MAX_TEXTURE_SIZE:${textureMaxSize[0]}; cell:${cellWidth}x${cellHeight}; cell_count:${getGLCharsMapSize()}; font_size:${size}; pad_x:${padX}; pad_y:${padY};)")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        canvas!!.setBitmap(null)
        canvas = null

        bitmap!!.recycle()
        bitmap = null

        // setup the array of character texture regions
        x = 0f
        y = 0f
        charMapSorted.forEach {
            it.setTextureRegion(GLTextureRegion(textureSize.toFloat(), textureSize.toFloat(), x, y, cellWidth - 1, cellHeight - 1))
            x += cellWidth
            if (x + cellWidth > textureSize) {
                x = 0f
                y += cellHeight
            }
        }
        // create full texture region
        textureRegion = GLTextureRegion(textureSize.toFloat(), textureSize.toFloat(), 0f, 0f, textureSize.toFloat(), textureSize.toFloat())

        return textureId
    }

    fun unload() {
        charMap.clear()
        if (textureId >= 0) GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
        textureId = -1
    }
}