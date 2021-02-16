/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.presenter

import android.graphics.PointF
import android.opengl.GLSurfaceView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.fromfinalform.blocks.domain.model.game.mode.classic.ClassicGameConfig
import com.fromfinalform.blocks.data.model.game.ClassicGameFieldBackground
import com.fromfinalform.blocks.domain.interactor.BlockBuilder
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.dagger.DaggerGameComponent
import com.fromfinalform.blocks.presentation.dagger.GameComponent
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.toRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.*
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.texture.ITextureRepository
import com.fromfinalform.blocks.presentation.model.repository.ShaderDrawerRepository
import com.fromfinalform.blocks.presentation.model.repository.TextureRepository
import com.fromfinalform.blocks.presentation.view.App
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import javax.inject.Inject

@InjectViewState
class GamePresenter : MvpPresenter<GamePresenter.GameView>(), LifecycleObserver {

    interface GameView : MvpView {
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun requestRender()
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun setRenderMode(mode: Int)

        @StateStrategyType(value = AddToEndSingleStrategy::class) fun onSceneConfigured(params: SceneParams)
    }

    val renderer: IRenderer get() = glViewRenderer
    private lateinit var gameComponent: GameComponent
    @Inject lateinit var gameLooper: IGameLooper

    private lateinit var glViewRenderer: ViewRenderer
    private /*lateinit*/ var config: IGameConfig = ClassicGameConfig()
    private /*lateinit*/ var textureRepo: ITextureRepository = TextureRepository(App.getApplicationContext())

    override fun onFirstViewAttach() {

    }

    var ru2: RenderUnit? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(owner: LifecycleOwner) {
        gameComponent = DaggerGameComponent.create()
        gameComponent.injectGamePresenter(this)

        glViewRenderer = ViewRenderer(0xFF20242F, Size(config.fieldWidthPx, config.fieldHeightPx))
            .withUpdater { viewState.requestRender() }
            .withListener(object : RendererListener {

                lateinit var sceneParams: SceneParams
                var ru: RenderUnit? = null

                var go: GameObject? = null
                var pt: PointF? = null

                override fun onFirstFrame() {

                }

                override fun onStart() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY) }
                override fun onStop() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY) }

                override fun onCrash() {}
                override fun onFrame(renderParams: RenderParams, sceneParams: SceneParams) {
//                    looper.onFrame(frame, timeMs, deltaTimeMs)

                    //ru?.withRotation(frame / 2f)

                    //go!!.translateX(0.5f)
                    //glViewRenderer.renderUnits.mapLayout(go!!, sceneParams)


//                    var p = PointF(ru2!!.x, ru2!!.y)
//                    rotateGLPoint(p, pt!!, ru2!!.itemParams.angle + 1f)
//                    ru2!!.withLocation(p.x, p.y)

                    //ru2!!.itemParams.rotate(pt!!, ru2!!.itemParams.angle + 1f)

                    //ru2!!.rotate(0.1f)
                }

                override fun onSceneConfigured(params: SceneParams) {
                    viewState.onSceneConfigured(params)

                    this.sceneParams = params
                    RenderUnit.shaderRepo = ShaderDrawerRepository().apply { initialize() }

                    var cw = config.blockWidthPx * sceneParams.sx
                    var ch = config.blockHeightPx * sceneParams.sy

                    glViewRenderer.clearRenderUnits()

                    ru = ClassicGameFieldBackground().build(config).toRenderUnit(sceneParams)
                    glViewRenderer.add(ru!!)

                    go = BlockBuilder(config).withTypeId(BlockTypeId._128).build()
//                    go!!.translateX(config.blockGapHPx)
//                    go!!.translateX(config.fieldWidthPx / 2)
//                    go!!.translateY(config.fieldHeightPx / 2)
                    ru2 = go!!.toRenderUnit(sceneParams)
                    ru2!!.translateXY(0f, 0f)

                    pt = PointF(ru2!!.x, ru2!!.y)

                    glViewRenderer.add(ru2!!)
                }
            })

    }
}