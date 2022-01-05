/*
 * Created by S.Dobranos on 07.02.21 21:54
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.mapper

import android.graphics.PointF
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimationTypeId
import com.fromfinalform.blocks.presentation.model.graphics.animation.*
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.text.resolver.GLTextResolver

class GraphicsMapper {
    companion object {

        fun List<GameObject>.toRenderUnit(renderParams: RenderParams, sceneParams: SceneParams) = this.map { it.toRenderUnit(renderParams, sceneParams) }
        fun GameObject.toRenderUnit(renderParams: RenderParams, sceneParams: SceneParams): RenderUnit {
            return RenderUnit().also {
                it.withId(this.id)
                it.map(this, renderParams, sceneParams)
                it.itemParams.withOnChanged {
                    val location = PointF(it.dstRect.left, it.dstRect.top).toGameLocation(sceneParams)
                    val size = PointF(it.dstRect.width(), it.dstRect.heightInv()).toGameSize(sceneParams)
                    this.x = location.x
                    this.y = location.y
                    this.width = size.x
                    this.height = size.y
                    this.alpha = it.alpha
                    this.rotation = it.angle
                }

                if (this.childs != null)
                    for (c in ArrayList(this.childs))
                        it.addChild(c.toRenderUnit(renderParams, sceneParams))
            }
        }

        fun GameObject.map(src: IRenderItem?, renderItem: RenderItem, sceneParams: SceneParams) {
            if (src == null)
                return


        }

        fun IRenderItem.map(src: GameObject?, renderParams: RenderParams, sceneParams: SceneParams) {
            if (src == null)
                return

            val ru = this as? RenderUnit
            if (ru == null)
                return

            val sceneXY = src.location.toSceneLocation(sceneParams)
            val sceneWH = src.size.toSceneSize(sceneParams)
            ru.setLayout(sceneXY, sceneWH, src.rotation, src.alpha)
            ru.withTag(src.tag)

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
                var glAnimation: IGLAnimation? = null
                var glTimeline: GLAnimationTimeline? = null

                if (it.typeId == GameObjectAnimationTypeId.TRANSLATE)
                    glTimeline = ru.addAnimation(TranslateTo(it.dstXY.toSceneLocation(sceneParams), it.speed, it.delay, it.interpolator)
                        .apply { glAnimation = this }, it.timelineId)

                if (it.typeId == GameObjectAnimationTypeId.SCALE)
                    glTimeline = ru.addAnimation(Scale(it.scaleTo, it.scaleFrom, it.duration, it.delay, it.interpolator)
                        .apply {
                            glAnimation = this
                            if (it.affectChilds)
                                withAffectChilds()
                        }, it.timelineId)

                if (it.typeId == GameObjectAnimationTypeId.ROTATE)
                    glTimeline = ru.addAnimation(RotateTo(it.dstAngle, it.speed, it.delay, it.interpolator)
                        .apply { glAnimation = this }, it.timelineId)

                if (it.completeHandler != null && glAnimation is GLCompletableAnimation<*>) {
                    (glAnimation as GLCompletableAnimation<*>).withOnComplete { e ->
                        it.completeHandler!!.invoke(e.args.renderItem.id)
                    }
                }

                if (src.animationQueueCompleteHandler != null) {
                    glTimeline!!.withOnQueueComplete { e ->
                        return@withOnQueueComplete src.animationQueueCompleteHandler?.invoke(src, e) ?: true
                    }
                }

                src.onAnimationConsumed(it)
            }

            //ru.childs?.forEach { ric -> ric.map(src.childs?.firstOrNull { goc -> goc.id == ric.id }, params) }
        }

        fun PointF.toSceneLocation(params: SceneParams): PointF {
            return PointF(-1.0f + x * params.sx, 1.0f - y * params.sy)
        }

        fun PointF.toSceneSize(params: SceneParams): PointF {
            return PointF(x * params.sx, y * params.sy)
        }

        fun PointF.toGameLocation(params: SceneParams): PointF {
            return PointF(/*ceil*/((x + 1.0f) / params.sx), /*ceil*/(-(y - 1.0f) / params.sy))
        }

        fun PointF.toGameSize(params: SceneParams): PointF {
            return PointF(/*ceil*/(x / params.sx), /*ceil*/(y / params.sy))
        }
    }
}