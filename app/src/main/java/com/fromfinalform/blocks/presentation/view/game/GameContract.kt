package com.fromfinalform.blocks.presentation.view.game

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.fromfinalform.blocks.core.mvi.common.CommonEffect
import com.fromfinalform.blocks.core.mvi.common.CommonEvent
import com.fromfinalform.blocks.core.mvi.common.CommonState
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams

sealed class GameState(
) : CommonState() {
    object Init : GameState()
    data class Rendering(val renderMode: Int) : GameState()
}

sealed class GameEffect : CommonEffect() {
    class RendererCreated(val renderer: GLSurfaceView.Renderer): GameEffect()
    class RendererQueueAction(val action: ()->Unit): GameEffect()

    class SceneConfigured(val sceneParams: SceneParams, var gameConfig: IGameConfig) : GameEffect()
    class SceneRender() : GameEffect()

    class BlockCurrentChanged(val type: BlockType?) : GameEffect()
}

sealed class GameEvent : CommonEvent() {
    object Start : GameEvent()
    object Pause : GameEvent()
    object Stop : GameEvent()

    data class Touch(val motionEvent: MotionEvent) : GameEvent()
}