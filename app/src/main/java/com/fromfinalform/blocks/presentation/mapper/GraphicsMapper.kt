/*
 * Created by S.Dobranos on 07.02.21 21:54
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.mapper

import android.graphics.PointF
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimationTypeId
import com.fromfinalform.blocks.presentation.model.graphics.animation.TranslateTo
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.text.resolver.GLTextResolver

class GraphicsMapper {
    companion object {

        fun List<GameObject>.toRenderUnit(renderParams: RenderParams, sceneParams: SceneParams) = this?.map { it.toRenderUnit(renderParams, sceneParams) }
        fun GameObject.toRenderUnit(renderParams: RenderParams, sceneParams: SceneParams): RenderUnit {
            return RenderUnit().also {
                it.withId(this.id)
                it.map(this, renderParams, sceneParams)

                if (this.childs != null)
                    for (c in ArrayList(this.childs))
                        it.addChild(c.toRenderUnit(renderParams, sceneParams))
            }
        }

        fun IRenderItem.map(src: GameObject?, renderParams: RenderParams, sceneParams: SceneParams) {
            if (src == null)
                return

            val ru = this as? RenderUnit
            if (ru == null)
                return

            val sceneXY = PointF(src.x, src.y).toScene(sceneParams)
            ru.setLayout(sceneXY.x, sceneXY.y, src.width * sceneParams.sx, src.height * sceneParams.sy, src.rotation, src.alpha)

            if (src.textStyle != null) {
                ru.withShader(ShaderDrawerTypeId.TEXT)
                ru.withTextResolver(GLTextResolver(renderParams.repos.textTexture[src.textStyle!!], sceneParams))
            } else if (src.assetId != null) {
                ru.withShader(ShaderDrawerTypeId.FLAT)
                ru.withTexture(renderParams.repos.texture[src.assetId!!])
            } else if (src.color != null) {
                ru.withShader(ShaderDrawerTypeId.SOLID)
                ru.withColor(src.color!!)
            }

            src.animations?.toList()?.forEach {
                if (it.typeId == GameObjectAnimationTypeId.TRANSLATE) {
                    ru.addAnimation(TranslateTo(it.dstXY.toScene(sceneParams), it.speed, interpolator = it.interpolator))
                }
            }

            //ru.childs?.forEach { ric -> ric.map(src.childs?.firstOrNull { goc -> goc.id == ric.id }, params) }
        }

        fun PointF.toScene(params: SceneParams): PointF {
            return PointF(-1 + x * params.sx, 1 - y * params.sy)
        }
    }
}