/*
 * Created by S.Dobranos on 09.02.21 0:32
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.text

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF

class TextLine(
    val index: Int,
    val bounds: RectF,
    val lineText: String,
    val startTextIndetInBaseText: Int,
    val endTextIndetInBaseText: Int,
    val baseLine: Float,
    val ascent: Float,
    val descent: Float,
    val containerMaxWH: PointF = PointF(),
    paddingLT: PointF? = null/*backward compatibility*/,
    //val author: TextLineAuthor? = null
) {

    var paddingLT = paddingLT; private set

    // not current line, whole text (bundle of lines)
    private var paddingsImpl: Rect? = null
    val paddings get() = paddingsImpl?.let { Rect(it) }
    val paddingStart get() = paddingsImpl?.left ?: 0f
    val paddingEnd get() = paddingsImpl?.right ?: 0f
    val paddingTop get() = paddingsImpl?.top ?: 0f
    val paddingBottom get() = paddingsImpl?.bottom ?: 0f

    fun withPaddings(value: Rect?): TextLine {
        this.paddingsImpl = value
        this.paddingLT = value?.let { PointF(it.left.toFloat(), it.top.toFloat()) }
        return this
    }

    fun getHeight() = ascent + descent

    //были проблемы с открытием шаблонов из версии 1.7 в 2.0, которые решились проверками на null в местех, где казалось бы, null быть не может (но он там есть). метод clone() из ObjectUtil тоже тут не работал
    fun clone() = TextLine(index, RectF(bounds), lineText ?: "", startTextIndetInBaseText, endTextIndetInBaseText, baseLine, ascent, descent, containerMaxWH ?: PointF(), if (paddingLT == null) null else PointF(paddingLT!!.x, paddingLT!!.y))
        .withPaddings(paddingsImpl)

}