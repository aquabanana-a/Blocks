package com.fromfinalform.blocks.presentation.model.graphics.text

import android.view.Gravity
import android.view.View
import java.io.Serializable

data class TextStyle(
    var text: String,
    var textSize: Float,
    var textFontId: Int,
    var textColor: Long = 0xFF000000,
    var textBackColor: Long = 0x00000000,
    var textAlign: Int = View.TEXT_ALIGNMENT_TEXT_START//View.TEXT_ALIGNMENT_CENTER
) : Serializable {

    var lineSpaceMultiplier = 1.0f//; private set
    var innerGravity = Gravity.START or Gravity.TOP or Gravity.BOTTOM//Gravity.CENTER

    var paddingLeft = 0f
    var paddingRight = 0f
    var paddingTop = 0f
    var paddingBottom = 0f

    fun withLineSpaceMultiplier(value: Float): TextStyle {
        this.lineSpaceMultiplier = value
        return this
    }

    fun withInnerGravity(value: Int): TextStyle {
        this.innerGravity = value
        return this
    }

    fun withPaddings(left: Float = 0f, top: Float = 0f, right: Float = 0f, bottom: Float = 0f): TextStyle {
        this.paddingLeft = left
        this.paddingTop = top
        this.paddingRight = right
        this.paddingBottom = bottom
        return this
    }

    // add here ref types clone for future needs
    fun clone(): TextStyle {
        val ret = this.copy()
            .withLineSpaceMultiplier(lineSpaceMultiplier)
            .withInnerGravity(innerGravity)
            .withPaddings(paddingLeft, paddingTop, paddingRight, paddingBottom)
//        if (animations != null)
//            ret.animations = ArrayList(this.animations!!.map { it.clone().let { v -> v.isVariant = it.isVariant;v } })
        return ret
    }

    // !Note: equals & hashCode we take from internal impl data class

}