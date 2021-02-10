/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.presenter

import android.opengl.GLSurfaceView
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.data.model.game.ClassicGameConfig
import com.fromfinalform.blocks.data.model.game.ClassicGameFieldBackground
import com.fromfinalform.blocks.domain.interactor.BlockBuilder
import com.fromfinalform.blocks.domain.model.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.GameObject
import com.fromfinalform.blocks.domain.model.game.IGameConfig
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.toRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.*
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.text.TextStyle
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextureRepository
import com.fromfinalform.blocks.presentation.model.repository.ShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.repository.TextureRepository
import com.fromfinalform.blocks.presentation.view.App
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@InjectViewState
class GamePresenter : MvpPresenter<GamePresenter.GameView>(), LifecycleObserver {

    interface GameView : MvpView {
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun requestRender()
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun setRenderMode(mode: Int)

        @StateStrategyType(value = AddToEndSingleStrategy::class) fun onSceneConfigured(params: SceneParams)
    }

    val renderer: IRenderer get() = glViewRenderer

    private lateinit var glViewRenderer: ViewRenderer
    private /*lateinit*/ var config: IGameConfig = ClassicGameConfig()
    private /*lateinit*/ var textureRepo: ITextureRepository = TextureRepository(App.getApplicationContext())

    override fun onFirstViewAttach() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(owner: LifecycleOwner) {
        glViewRenderer = ViewRenderer(0xFF20242F, Size(config.fieldWidthPx, config.fieldHeightPx))
            .withUpdater { viewState.requestRender() }
            .withListener(object : RendererListener {

                var ru: RenderUnit? = null

                override fun onStart() {
                    viewState.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
                }

                override fun onFirstFrame() {}
                override fun onStop() {
                    viewState.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
                }

                override fun onCrash() {}
                override fun onFrame(frame: Long, timeMs: Long, deltaTimeMs: Long) {
//                    looper.onFrame(frame, timeMs, deltaTimeMs)

                    ru?.withRotation(frame / 2f)
                }

                override fun onSceneConfigured(params: SceneParams) {
                    viewState.onSceneConfigured(params)

                    RenderUnit.shaderRepo = ShaderDrawerRepository().apply { initialize() }

                    var cw = config.blockWidthPx * params.sx
                    var ch = config.blockHeightPx * params.sy

                    glViewRenderer.clearRenderUnits()


//                    ru = RenderUnit()
//                        .withId(1)
//                        .withLocation(0f - cw/2, 0f + ch/2)
//                        .withSize(cw, ch)
//                        .withShader(ShaderDrawerTypeId.GRADIENT)
//                        .withColor(0xFFFF0000, 0xFF0000FF, 45)

//                    ru = RenderUnit()
//                        .withId(1)
//                        .withLocation(0f - cw / 2, 0f + ch / 2)
//                        .withSize(cw, ch)
//                        .withShader(ShaderDrawerTypeId.FLAT)
//                        .withTexture(textureRepo[R.drawable.bg_03])

                    ru = ClassicGameFieldBackground().build(config).toRenderUnit(params)
                    glViewRenderer.add(ru!!)

                    var go = BlockBuilder(config).withTypeId(BlockTypeId._128).build()
                    go.x = config.blockGapHPx

                    var goTxt = GameObject()
                    goTxt.x = config.blockGapHPx
                    goTxt.width = config.blockWidthPx
                    goTxt.height = config.blockHeightPx
                    goTxt.textStyle = TextStyle("BABa Milo karolina parsed wagen", 22f, R.font.comfortaa_regular, 0xFFFFFFFF, 0xFFFF0000, View.TEXT_ALIGNMENT_TEXT_START)
                    goTxt.textStyle!!.withInnerGravity(Gravity.CENTER)
                    goTxt.textStyle!!.withPaddings(left = 10f, right = 20f, top = 60f, bottom = 20f)

                    go.childs = arrayListOf()
                    (go.childs as MutableList).add(goTxt)

                    var ru2 = go.toRenderUnit(params)
                    glViewRenderer.add(ru2)
                }
            })

    }
}