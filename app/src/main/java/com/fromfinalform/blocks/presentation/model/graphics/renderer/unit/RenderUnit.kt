/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.opengl.GLES20
import com.fromfinalform.blocks.presentation.model.graphics.drawer.IShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.IRenderer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLGradient
import io.instories.core.render.resolver.GLTextResolver

class RenderUnit : RenderItem(), IRenderUnit {

    companion object {
        var shaderRepo: IShaderDrawerRepository? = null
    }

    private var textResolver: GLTextResolver? = null
    fun withTextResolver(value: GLTextResolver): RenderUnit {
        this.textResolver = value
        return this
    }

    override fun render(renderer: IRenderer, params: SceneParams, timeMs: Long, deltaTimeMs: Long) {
        renderImpl(this, renderer, params, timeMs, deltaTimeMs)
    }

    private fun renderImpl(item: RenderItem, renderer: IRenderer, params: SceneParams, timeMs: Long, deltaTimeMs: Long) {
        val drawer = shaderRepo!![item.shaderTypeId]

        when (item.shaderTypeId) {
            ShaderDrawerTypeId.SOLID    -> drawer!!.setUniforms(GLColor(item.color))
            ShaderDrawerTypeId.FLAT     -> drawer!!.setUniforms(item.textureId ?: -1)
            ShaderDrawerTypeId.GRADIENT -> drawer!!.setUniforms(GLGradient(item.color, if (item.colorSecondary > -1) item.colorSecondary else null, item.colorAngle))
            ShaderDrawerTypeId.TEXT     -> drawer!!.setUniforms((item as RenderUnit).textResolver!!)
        }

        if (item.usedBlend) {
            GLES20.glEnable(GLES20.GL_BLEND)
            if (item.usedBlendSeparate) GLES20.glBlendFuncSeparate(item.blendSrcRGB!!, item.blendDstRGB!!, item.blendSrcAlpha!!, item.blendDstAlpha!!)
            else if (item.usedBlendFactor) GLES20.glBlendFunc(item.blendSrc, item.blendDst)
        } else GLES20.glDisable(GLES20.GL_BLEND)

        if (drawer != null) {
            drawer.draw(this, params, item.dstRect, item.srcRect, item.rotation)
            drawer.cleanUniforms()
        }

        if (item.childs != null)
            for (c in item.childs!!)
                renderImpl(c, renderer, params, timeMs, deltaTimeMs)
    }

    override fun prerender(renderer: IRenderer) {

    }

    override fun postrender(renderer: IRenderer) {

    }


}