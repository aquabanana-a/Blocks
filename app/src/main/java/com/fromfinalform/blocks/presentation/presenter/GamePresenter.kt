/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.presenter

import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.dagger.DaggerGameComponent
import com.fromfinalform.blocks.presentation.dagger.GameComponent
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.map
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.toRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.animation.Alpha
import com.fromfinalform.blocks.presentation.model.graphics.animation.RotateBy
import com.fromfinalform.blocks.presentation.model.graphics.animation.TranslateTo
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

    interface GamePropertyView {
        val surfaceView: GLSurfaceView
    }

    interface GameView : MvpView {
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun requestRender()
        @StateStrategyType(value = AddToEndSingleStrategy::class) fun setRenderMode(mode: Int)

        @StateStrategyType(value = AddToEndSingleStrategy::class) fun onSceneConfigured(params: SceneParams)
    }

    val viewProperty get(): GamePropertyView? = attachedViews.firstOrNull { it is GamePropertyView } as? GamePropertyView

    val renderer: IRenderer get() = glViewRenderer
    private lateinit var gameComponent: GameComponent
    @Inject protected lateinit var gameLooper: IGameLooper
    @Inject protected lateinit var gameConfig: IGameConfig

    private lateinit var glViewRenderer: ViewRenderer
    private /*lateinit*/ var textureRepo: ITextureRepository = TextureRepository(App.getApplicationContext())

    private fun postGl(handler: ()->Unit) {
        if(viewProperty == null)
            return
        viewProperty!!.surfaceView.queueEvent(handler)
    }

    fun startGame() { postGl {
        gameLooper.start()
    } }

    fun stopGame() {

    }

    override fun onFirstViewAttach() {

    }

    var ru2: RenderUnit? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(owner: LifecycleOwner) {
        gameComponent = DaggerGameComponent.create()
        gameComponent.injectGamePresenter(this)

        glViewRenderer = ViewRenderer(0xFF20242F, Size(gameConfig.fieldWidthPx, gameConfig.fieldHeightPx))
            .withUpdater { viewState.requestRender() }
            .withListener(object : RendererListener {

                lateinit var sceneParams: SceneParams

                override fun onFirstFrame() {

                }

                override fun onStart() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY) }
                override fun onStop() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY) }

                override fun onCrash() {}
                override fun onFrame(renderParams: RenderParams, sceneParams: SceneParams) {
                    gameLooper.objectsDirtyFlat.forEach {
                        val ru = glViewRenderer.getRenderUnit(it.id)
                        ru?.map(it, sceneParams)
                        it.onDrawn()
                    }
                }

                override fun onSceneConfigured(params: SceneParams) {
                    viewState.onSceneConfigured(params)

                    this.sceneParams = params

                    gameLooper.init()
                    gameLooper.withObjectsCountChangedListener { toAdd, toRemove ->
                        if (toAdd != null) {
                            val ru = toAdd.toRenderUnit(sceneParams).first()
                            ru.withLocation(-1f, 1f)
                            ru.addAnimation(Alpha(1f, 0f, 500, interpolator = LinearInterpolator()))
                            ru.addAnimation(TranslateTo(PointF(0.5f, -0.6f), 0.0005f, interpolator = BounceInterpolator()))
                            ru.addAnimation(RotateBy(900f, 0.1f, interpolator = BounceInterpolator()))
                            glViewRenderer.add(ru)
                        }
                    }
                    viewProperty!!.surfaceView.setOnTouchListener { v, me -> gameLooper.onTouch(me, params) }

                    RenderUnit.shaderRepo = ShaderDrawerRepository().apply { initialize() }

                    var cw = gameConfig.blockWidthPx * sceneParams.sx
                    var ch = gameConfig.blockHeightPx * sceneParams.sy

                    glViewRenderer.clearRenderUnits()

//                    ru = ClassicGameFieldBackground().build(gameConfig).toRenderUnit(sceneParams)
//                    glViewRenderer.add(ru!!)
//
//                    go = BlockBuilder(gameConfig).withTypeId(BlockTypeId._128).build()
////                    go!!.translateX(config.blockGapHPx)
////                    go!!.translateX(config.fieldWidthPx / 2)
////                    go!!.translateY(config.fieldHeightPx / 2)
//                    ru2 = go!!.toRenderUnit(sceneParams)
//                    ru2!!.translateXY(0f, 0f)
//
//                    pt = PointF(ru2!!.x, ru2!!.y)

                    glViewRenderer.add(gameLooper.objects.toRenderUnit(sceneParams))
                }
            })

    }
}