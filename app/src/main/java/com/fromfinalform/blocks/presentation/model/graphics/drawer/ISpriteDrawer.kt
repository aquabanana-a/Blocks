package com.fromfinalform.blocks.presentation.model.graphics.drawer

import android.graphics.PointF
import android.graphics.RectF
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLTextureRegion

interface ISpriteDrawer {
    fun drawSprite(x: Float, y: Float, width: Float, height: Float, region: GLTextureRegion, colors: Array<GLColor>, clip: RectF?, useTextureColors: Boolean = false, endBatch: Boolean = false)

    fun drawSprite(vertices: Array<PointF>, region: GLTextureRegion, colors: Array<GLColor>/*[lb rb rt lt]*/, clip: RectF?, useTextureColors: Boolean = false, endBatch: Boolean = false)
}