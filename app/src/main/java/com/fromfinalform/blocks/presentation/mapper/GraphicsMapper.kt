/*
 * Created by S.Dobranos on 07.02.21 21:54
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.mapper

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
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

        fun GameObject.toRenderUnit(params: SceneParams): RenderUnit {
            return RenderUnit().also {
                it.withId(this.id)
                it.mapLayout(this, params)

                if (this.childs != null)
                    for (c in ArrayList(this.childs))
                        it.addChild(c.toRenderUnit(params))

                if (this.textStyle != null) {
                    it.withShader(ShaderDrawerTypeId.TEXT)
                    it.withTextResolver(GLTextResolver(textTextureRepo[this.textStyle!!], params))
                }
                else if (this.assetId != null) {
                    it.withShader(ShaderDrawerTypeId.FLAT)
                    it.withTexture(textureRepo[this.assetId!!])
                }
                else if (this.color != null) {
                    it.withShader(ShaderDrawerTypeId.SOLID)
                    it.withColor(this.color!!)
                }
            }
        }

        fun IRenderItem.mapLayout(src: GameObject, params: SceneParams) = arrayListOf(this).mapLayout(arrayListOf(src), params)
        fun List<IRenderItem>.mapLayout(src: GameObject, params: SceneParams) = this.mapLayout(arrayListOf(src), params)
        fun List<IRenderItem>.mapLayout(src: List<GameObject>?, params: SceneParams) {
            if (src == null)
                return

            for (go in src)
                for (iri in this) {
                    val ri = iri as? RenderItem
                    if (ri?.id == go.id) {
                        ri.setLayout(-1 + go.x * params.sx, 1 - go.y * params.sy, go.width * params.sx, go.height * params.sy, go.rotation)
                        ri.childs?.mapLayout(go.childs, params)
                    }
                }
        }
    }
}