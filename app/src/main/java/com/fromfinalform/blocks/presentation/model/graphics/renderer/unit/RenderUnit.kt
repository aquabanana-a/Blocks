/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import android.opengl.GLES20
import com.fromfinalform.blocks.presentation.model.graphics.animation.IGLCompletableAnimation
import com.fromfinalform.blocks.presentation.model.graphics.animation.event.GLAnimationCompleteArgs
import com.fromfinalform.blocks.presentation.model.graphics.drawer.ShaderDrawerTypeId
import com.fromfinalform.blocks.presentation.model.graphics.renderer.IRenderer
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLGradient
import com.fromfinalform.blocks.presentation.model.graphics.text.resolver.GLTextResolver

class RenderUnit : RenderItem(), IRenderUnit {

    private var textResolver: GLTextResolver? = null
    fun withTextResolver(value: GLTextResolver): RenderUnit {
        this.textResolver = value
        return this
    }

    override fun render(renderer: IRenderer, renderParams: RenderParams, sceneParams: SceneParams) {
        renderImpl(this, renderer, renderParams, sceneParams)
    }

    private fun renderImpl(item: RenderItem, renderer: IRenderer, renderParams: RenderParams, sceneParams: SceneParams, parent: RenderItem? = null) {
        val drawer = renderParams.repos.shader[item.shaderTypeId]

        val animationsCompleted = arrayListOf<IGLCompletableAnimation>()
        item.timelines.values.forEach {
            val a = it.currentAnimation
            if (a != null) {
                if (!a.isInitialized) a.initialize(item, renderParams, sceneParams)
                a.transform(item, renderParams, sceneParams)

                if (a is IGLCompletableAnimation && a.isComplete)
                    animationsCompleted.add(a)
            }
        }

//        item.animations?.toList()?.forEach {
//            if (!it.isInitialized) it.initialize(item, renderParams, sceneParams)
//            it.transform(item, renderParams, sceneParams)
//
//            if (it is IGLCompletableAnimation && it.isComplete)
//                animationsToRemove.add(it)
//        }

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

        item.childs?.mapNotNull { it as? RenderItem }?.forEach {
            renderImpl(it, renderer, renderParams, sceneParams, item)
        }

        animationsCompleted.forEach {
            it.onComplete(GLAnimationCompleteArgs(item, renderParams, sceneParams))
        }
//        if (animationsCompleted.size > 0) {
//            animationsCompleted.forEach {
//                item.removeAnimation(it)
//                it.onComplete(GLAnimationCompleteArgs(item, renderParams, sceneParams))
//            }
////            renderer.scope.launch { animationsToRemove.forEach { it.completeHandler?.invoke(item, renderParams, sceneParams) } }
//        }
    }

    override fun prerender(renderer: IRenderer) {

    }

    override fun postrender(renderer: IRenderer) {

    }


}