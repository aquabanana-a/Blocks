/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.presenter

import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.interactor.BlockBuilder
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import com.fromfinalform.blocks.presentation.dagger.DaggerGameComponent
import com.fromfinalform.blocks.presentation.dagger.GameComponent
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.map
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.toRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.animation.Alpha
import com.fromfinalform.blocks.presentation.model.graphics.animation.RotateBy
import com.fromfinalform.blocks.presentation.model.graphics.animation.TranslateTo
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

        @StateStrategyType(value = AddToEndSingleStrategy::class) fun onCurrentBlockChanged(type: BlockType?)
    }

    val viewProperty get(): GamePropertyView? = attachedViews.firstOrNull { it is GamePropertyView } as? GamePropertyView

    val renderer: IRenderer get() = glViewRenderer
    private lateinit var gameComponent: GameComponent
    @Inject protected lateinit var gameLooper: IGameLooper
    @Inject lateinit var gameConfig: IGameConfig
    @Inject protected lateinit var blockTypeRepo: IBlockTypeRepository

    private lateinit var glViewRenderer: ViewRenderer

    private fun postGl(handler: ()->Unit) {
        if(viewProperty == null)
            return
        viewProperty!!.surfaceView.queueEvent(handler)
    }

    fun startGame() { postGl {
        gameLooper.start()
    } }

    fun stopGame() { postGl {

    } }

    override fun onFirstViewAttach() {

    }

    lateinit var sceneParams: SceneParams

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(owner: LifecycleOwner) {
        gameComponent = DaggerGameComponent.create()
        gameComponent.injectGamePresenter(this)

        glViewRenderer = ViewRenderer(0xFF20242F, Size(gameConfig.canvasWidthPx, gameConfig.canvasHeightPx))
            .withUpdater { viewState.requestRender() }
            .withListener(object : RendererListener {


                override fun onFirstFrame() {

                }

                override fun onStart() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY) }
                override fun onStop() { viewState.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY) }

                override fun onCrash() {}
                override fun onFrame(renderParams: RenderParams, sceneParams: SceneParams) {
                    gameLooper.objectsDirtyFlat.forEach {
                        val ru = glViewRenderer.getRenderUnit(it.id)
                        if (ru != null) {
                            if (it.isWaitForRemove) {
                                if (ru.parent != null)
                                    ru.parent!!.removeChild(ru.id)
                                else
                                    glViewRenderer.removeRenderUnit(ru.id)
                                it.onRemoved()
                            }
                            else
                                ru.map(it, renderParams, sceneParams)
                        }
                        else if (it.parent == null)
                            glViewRenderer.add(it.toRenderUnit(renderParams, sceneParams))
                        it.onDrawn()
                    }

                    gameLooper.onFrameDrawn(renderParams, sceneParams)
                }

                override fun onSceneConfigured(renderRepo: RenderRepo, params: SceneParams) {
                    viewState.onSceneConfigured(params)

                    sceneParams = params

                    gameLooper.init()
                    gameLooper.currentBlock.subscribe { viewState.onCurrentBlockChanged(if (it != null) blockTypeRepo[it.typeId] else null) }

                    viewProperty!!.surfaceView.setOnTouchListener { v, me -> gameLooper.onTouch(me, params) }

                    var cw = gameConfig.blockWidthPx * sceneParams.sx
                    var ch = gameConfig.blockHeightPx * sceneParams.sy

//                    var sm = System.currentTimeMillis()
//                    var ts = TextStyle("512", 28f, R.font.jura_bold, 0xFFFFFFFF, 0xFFFF0000).withInnerGravity(Gravity.CENTER)
//                    var texture = renderRepo.textTexture[ts]
//                    var tm = System.currentTimeMillis() - sm
//
//                    Log.d("mymy", "#1 GLText load time: ${tm}ms")
//
//                    sm = System.currentTimeMillis()
//                    ts = TextStyle("128", 20f, R.font.airfool, 0xFFFFFF00, 0xFFFF0000).withInnerGravity(Gravity.CENTER)
//                    texture = renderRepo.textTexture[ts]
//                    tm = System.currentTimeMillis() - sm
//
//                    Log.d("mymy", "#2 GLText load time: ${tm}ms")
//
//                    sm = System.currentTimeMillis()
//                    ts = TextStyle("1024", 24f, R.font.comfortaa_regular, 0xFFF4FF09, 0xFFFF0000).withInnerGravity(Gravity.CENTER)
//                    texture = renderRepo.textTexture[ts]
//                    tm = System.currentTimeMillis() - sm
//
//                    Log.d("mymy", "#3 GLText load time: ${tm}ms")
//
//                    sm = System.currentTimeMillis()
//                    ts = TextStyle("128", 20f, R.font.airfool, 0xFFFFFF00, 0xFFFF0000).withInnerGravity(Gravity.CENTER)
//                    texture = renderRepo.textTexture[ts]
//                    tm = System.currentTimeMillis() - sm
//
//                    Log.d("mymy", "#4 GLText load time: ${tm}ms")

                }
            })

    }
}