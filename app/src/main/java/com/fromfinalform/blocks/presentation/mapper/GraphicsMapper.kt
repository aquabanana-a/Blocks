/*
 * Created by S.Dobranos on 07.02.21 21:54
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.mapper

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextTextureRepository
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextureRepository
import com.fromfinalform.blocks.presentation.model.repository.TextTextureRepository
import com.fromfinalform.blocks.presentation.model.repository.TextureRepository
import com.fromfinalform.blocks.presentation.view.App
import io.instories.core.render.resolver.GLTextResolver

class GraphicsMapper {
    companion object {

        private var textureRepo: ITextureRepository = TextureRepository(App.getApplicationContext())
        private var textTextureRepo: ITextTextureRepository = TextTextureRepository(App.getApplicationContext())

        fun List<GameObject>.toRenderUnit(params: SceneParams) = this?.map { it.toRenderUnit(params) }
        fun GameObject.toRenderUnit(params: SceneParams): RenderUnit {
            return RenderUnit().also {
                it.withId(this.id)
                it.map(this, params)

                if (this.childs != null)
                    for (c in ArrayList(this.childs))
                        it.addChild(c.toRenderUnit(params))
            }
        }

        fun IRenderItem.map(src: GameObject?, params: SceneParams) {
            if (src == null)
                return

            val ru = this as? RenderUnit
            if (ru == null)
                return

            ru.setLayout(-1 + src.x * params.sx, 1 - src.y * params.sy, src.width * params.sx, src.height * params.sy, src.rotation, src.alpha)

            if (src.textStyle != null) {
                ru.withShader(ShaderDrawerTypeId.TEXT)
                ru.withTextResolver(GLTextResolver(textTextureRepo[src.textStyle!!], params))
            } else if (src.assetId != null) {
                ru.withShader(ShaderDrawerTypeId.FLAT)
                ru.withTexture(textureRepo[src.assetId!!])
            } else if (src.color != null) {
                ru.withShader(ShaderDrawerTypeId.SOLID)
                ru.withColor(src.color!!)
            }

            ru.childs?.forEach { ric -> ric.map(src.childs?.firstOrNull { goc -> goc.id == ric.id }, params) }
        }
    }
}