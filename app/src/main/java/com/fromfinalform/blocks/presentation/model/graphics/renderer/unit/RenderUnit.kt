/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.opengl.GLES20
import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLAnimation
import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLCompletableAnimation
import com.fromfinalform.blocks.presentation.model.graphics.drawer.IShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.IRenderer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLGradient
import io.instories.core.render.resolver.GLTextResolver
import kotlinx.coroutines.launch

class RenderUnit : RenderItem(), IRenderUnit {

    companion object {
        var shaderRepo: IShaderDrawerRepository? = null
    }

    private var textResolver: GLTextResolver? = null
    fun withTextResolver(value: GLTextResolver): RenderUnit {
        this.textResolver = value
        return this
    }

    override fun render(renderer: IRenderer, renderParams: RenderParams, sceneParams: SceneParams) {
        renderImpl(this, renderer, renderParams, sceneParams)
    }

    private fun renderImpl(item: RenderItem, renderer: IRenderer, renderParams: RenderParams, sceneParams: SceneParams, parent: RenderItem? = null) {
        val drawer = shaderRepo!![item.shaderTypeId]

        val animationsToRemove = arrayListOf<IGLCompletableAnimation>()
        item.animations?.toList()?.forEach {
            if (!it.isInitialized) it.initialize(item, renderParams, sceneParams)
            it.transform(item, renderParams, sceneParams)

            if (it is IGLCompletableAnimation && it.isComplete)
                animationsToRemove.add(it)
        }

        if (animationsToRemove.size > 0) {
            animationsToRemove.forEach { item.removeAnimation(it) }
            renderer.scope.launch { animationsToRemove.forEach { it.completeHandler?.invoke(item, renderParams, sceneParams) } }
        }

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
            drawer.draw(this, sceneParams, item.itemParams, parent?.itemParams)
            drawer.cleanUniforms()
        }

        item.childs?.toList()?.forEach {
            renderImpl(it, renderer, renderParams, sceneParams, item)
        }
    }

    override fun prerender(renderer: IRenderer) {

    }

    override fun postrender(renderer: IRenderer) {

    }


}