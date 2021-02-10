package com.fromfinalform.blocks.presentation.model.graphics.renderer.data

class GLTextAlign {
    companion object {
        const val NONE              = 0

        const val START             = 1 shl 0
        const val END               = 1 shl 1
        const val TOP               = 1 shl 2
        const val BOTTOM            = 1 shl 3

        const val CENTER_HORIZONTAL = 1 shl 4
        const val CENTER_VERTICAL   = 1 shl 5
        const val CENTER            = CENTER_HORIZONTAL or CENTER_VERTICAL

        const val DEFAULT           = START or TOP
    }
}