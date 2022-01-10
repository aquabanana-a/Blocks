package com.fromfinalform.blocks.presentation.view.game

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.app.App
import com.fromfinalform.blocks.common.ui.extensions.viewBinding
import com.fromfinalform.blocks.core.di.findComponentDependenciesProvider
import com.fromfinalform.blocks.core.mvi.common.CommonFragment
import com.fromfinalform.blocks.core.mvi.base.Effect
import com.fromfinalform.blocks.core.mvi.base.State
import com.fromfinalform.blocks.databinding.FragmentGameBinding
import com.fromfinalform.blocks.presentation.model.graphics.opengl.EGL10ContextFactory

class GameFragment : CommonFragment<GameViewModel>(R.layout.fragment_game) {

    private val binding by viewBinding(FragmentGameBinding::bind)

    private var sceneConfigured = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnStart.setOnClickListener { dispatchEvent(GameEvent.Start) }
            btnStop.setOnClickListener { dispatchEvent(GameEvent.Stop) }
        }
    }

    override suspend fun renderState(state: State) {
        super.renderState(state)

        when (state) {
            is GameState.Rendering -> {
                binding.glSurface.renderMode = state.renderMode
            }
        }
    }

    override suspend fun handleEffect(effect: Effect) {
        super.handleEffect(effect)

        when (effect) {
            is GameEffect.RendererCreated -> {
                with(binding) {
                    glSurface.isVisible = true
                    glSurface.setEGLContextFactory(EGL10ContextFactory())
                    glSurface.setEGLContextClientVersion(2)
                    glSurface.setRenderer(effect.renderer)
                    glSurface.setOnTouchListener { v, me ->
                        dispatchEvent(GameEvent.Touch(motionEvent = me))
                        true
                    }
                }
            }
            is GameEffect.RendererQueueAction -> {
                with(binding) {
                    glSurface.queueEvent { effect.action.invoke() }
                }
            }
            is GameEffect.SceneConfigured -> {
                if (sceneConfigured) return

                val params = effect.sceneParams
                val config = effect.gameConfig
                with(binding) {
                    var lp = vgCanvasGroup.layoutParams as ConstraintLayout.LayoutParams
                    lp.width = params.scaledSceneWidth.toInt() //+ canvasFramePadding
                    lp.height = params.scaledSceneHeight.toInt() //+ canvasFramePadding
                    vgCanvasGroup.layoutParams = lp

                    var vgPanel_vMid = vgPanelBottom.findViewById<View>(R.id.v_mid)
                    lp = vgPanel_vMid.layoutParams as ConstraintLayout.LayoutParams
                    lp.width = ((2f * config.blockGapHPx + config.blockWidthPx) / params.scaleInv).toInt()
                    vgPanel_vMid.layoutParams = lp

                    var vgPanel_vTop = vgPanelBottom.findViewById<View>(R.id.v_top)
                    lp = vgPanel_vTop.layoutParams as ConstraintLayout.LayoutParams
                    lp.height = (config.blockCurrGapTopPx / params.scaleInv).toInt()
                    vgPanel_vTop.layoutParams = lp

                    var vgPanel_vBottom = vgPanelBottom.findViewById<View>(R.id.v_bottom)
                    lp = vgPanel_vBottom.layoutParams as ConstraintLayout.LayoutParams
                    lp.height = (config.blockCurrGapBottomPx / params.scaleInv).toInt()
                    vgPanel_vBottom.layoutParams = lp

                    lp = vgPanelBottom.layoutParams as ConstraintLayout.LayoutParams
                    lp.height = ((config.blockHeightPx + config.blockGapVPx + config.blockGapVPx + config.blockCurrGapTopPx + config.blockCurrGapBottomPx) / params.scaleInv).toInt()
                    vgPanelBottom.layoutParams = lp
                }
                sceneConfigured = true
            }
            is GameEffect.SceneRender -> {
                binding.glSurface.requestRender()
            }
            is GameEffect.BlockCurrentChanged -> {
                binding.tvStatus.text = "NEXT_BLOCK: ${effect.type?.id}"
            }
        }
    }
}