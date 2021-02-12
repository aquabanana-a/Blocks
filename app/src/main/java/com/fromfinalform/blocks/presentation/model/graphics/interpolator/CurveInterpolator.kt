package com.fromfinalform.blocks.presentation.model.graphics.interpolator

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.view.animation.Interpolator

class CurveInterpolator( val points:List<PointF?>?) :Interpolator{
    constructor( timeKeys:Iterable<Number>, values:Iterable<Number>):this(timeKeys.zip(values){time,value->PointF(time.toFloat()*1000,value.toFloat())})
    var curve:Path? = null
    var pm :PathMeasure?= null
    var coordinates:FloatArray? = null
    override fun getInterpolation(input: Float): Float {
        try {
            if (curve == null) curve = Path().apply {
                points?.filterNotNull()?.mapIndexed { index, pointF ->
                    if (index == 0) {
                        moveTo(pointF.x, pointF.y)
                    } else {
                        lineTo(pointF.x, pointF.y)
                    }
                }
            }
            if (pm == null) pm = PathMeasure(curve, false)
            if (coordinates == null) coordinates = floatArrayOf(0f, 0f)
            pm?.getPosTan((pm?.getLength() ?: 0f) * input, coordinates, null);
        }catch (e:Throwable){
            e.printStackTrace()
            return input
        }
        return coordinates?.get(1)?:input
    }
}