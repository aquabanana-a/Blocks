package com.fromfinalform.blocks.presentation.model.graphics.interpolator

class EaseInEaseOutInterpolator(val loopPeriods: Float = 0f) : TimeFuncInterpolator(0.42f, 0.0f, 0.58f, 1.0f) {
    override fun getInterpolation(input: Float): Float {
        return super.getInterpolation(if (loopPeriods == 0f) input else {
                val fraction = input * loopPeriods * 2f
                val iFraction = fraction.toInt()
                if (iFraction % 2 == 0) fraction - iFraction.toFloat()
                else (1f - (fraction - iFraction.toFloat()))
            }
        )
    }
}