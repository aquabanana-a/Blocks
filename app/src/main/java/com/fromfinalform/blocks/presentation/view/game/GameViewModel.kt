package com.fromfinalform.blocks.presentation.view.game

import android.opengl.GLSurfaceView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.fromfinalform.blocks.core.mvi.common.CommonViewModel
import com.fromfinalform.blocks.core.mvi.base.Event
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import com.fromfinalform.blocks.presentation.dagger.DaggerGameComponent
import com.fromfinalform.blocks.presentation.dagger.GameComponent
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.map
import com.fromfinalform.blocks.presentation.mapper.GraphicsMapper.Companion.toRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderRepo
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RendererListener
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.Size
import com.fromfinalform.blocks.presentation.model.graphics.renderer.ViewRenderer
import javax.inject.Inject

class GameViewModel : CommonViewModel() {

    private lateinit var gameComponent: GameComponent

    @Inject protected lateinit var gameLooper: IGameLooper
    @Inject protected lateinit var gameConfig: IGameConfig
    @Inject protected lateinit var blockTypeRepo: IBlockTypeRepository

    private lateinit var sceneParams: SceneParams
    private lateinit var glViewRenderer: ViewRenderer

    override val defaultState = GameState.Init

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.ordinal) {
            Lifecycle.Event.ON_CREATE.ordinal -> {
                gameComponent = DaggerGameComponent.create()
                gameComponent.injectGamePresenter(this)

                glViewRenderer = ViewRenderer(0xFF20242F, Size(gameConfig.canvasWidthPx, gameConfig.canvasHeightPx))
                    .withUpdater {
                        putEffect(GameEffect.SceneRender())
                    }
                    .withListener(object : RendererListener {
                        override fun onFirstFrame() {
                            gameLooper.init()
                            gameLooper.currentBlock.subscribe {
                                putEffect(GameEffect.BlockCurrentChanged(if (it!=null) blockTypeRepo[it.typeId] else null))
                            }
                        }

                        override fun onStart() {
                            (stateFlow.value as? GameState.Rendering)?.let {
                                applyState(it.copy(renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY))
                            }
                        }

                        override fun onStop() {
                            (stateFlow.value as? GameState.Rendering)?.let {
                                applyState(it.copy(renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY))
                            }
                        }

                        override fun onCrash() {}
                        override fun onFrame(renderParams: RenderParams, sceneParams: SceneParams) {
                            gameLooper.trashBlock.subscribe {
                                if (it==null)
                                    return@subscribe

                                glViewRenderer.removeRenderUnit(it.id)
                                it.onRemoved()
                            }
                            gameLooper.objectsDirtyFlat.forEach {
                                val ru = glViewRenderer.getRenderUnit(it.id)
                                if (ru!=null) {
                                    if (it.isWaitForRemove) {
                                        if (ru.parent!=null)
                                            ru.parent!!.removeChild(ru.id)
                                        else
                                            glViewRenderer.removeRenderUnit(ru.id)
                                        it.onRemoved()
                                    } else {
                                        ru.map(it, renderParams, sceneParams)
                                    }
                                } else if (it.parent==null)
                                    glViewRenderer.add(it.toRenderUnit(renderParams, sceneParams))

                                it.onDrawn()
                            }

                            gameLooper.onFrameDrawn(renderParams, sceneParams)
                        }

                        override fun onSceneConfigured(renderRepo: RenderRepo, params: SceneParams) {
                            putEffect(GameEffect.SceneConfigured(params, gameConfig))

                            sceneParams = params
                        }
                    })
                putEffect(GameEffect.RendererCreated(renderer = glViewRenderer))
                applyState(GameState.Rendering(renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY))

                putEffect(GameEffect.RendererQueueAction(action = {
                    glViewRenderer.start()
                }))
            }
        }
    }

    override suspend fun handleEvent(event: Event) {
        super.handleEvent(event)

        when (event) {
            is GameEvent.Start -> {
                putEffect(GameEffect.RendererQueueAction(action = {
                    gameLooper.start()
                }))
            }
            is GameEvent.Touch -> {
                gameLooper.onTouch(event.motionEvent, sceneParams)
            }
        }
    }
}