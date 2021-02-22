/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.view

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.presentation.model.graphics.opengl.EGL10ContextFactory
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.presenter.GamePresenter
import com.fromfinalform.blocks.presentation.view.common.dp
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class GameFragment : MvpAppCompatFragment(), GamePresenter.GameView, GamePresenter.GamePropertyView {

    private lateinit var vRoot: ConstraintLayout
    private lateinit var vgCanvasGroup: ConstraintLayout
    private lateinit var vgPanelBottomGroup: ConstraintLayout
    private lateinit var vHGap: View
    private lateinit var glSurface: GLSurfaceView
    private lateinit var tvStatus: TextView

    private var sceneConfigured = false

    @InjectPresenter
    lateinit var presenter: GamePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(presenter)

        sceneConfigured = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vRoot = inflater.inflate(R.layout.fragment_game, container, false) as ConstraintLayout

        vgCanvasGroup = vRoot.findViewById(R.id.vg_canvas_group)
        vgPanelBottomGroup = vRoot.findViewById(R.id.vg_panel_bottom)
//        vHGap = vRoot.findViewById(R.id.v_hgap)

        glSurface = vRoot.findViewById(R.id.gl_surface)
        glSurface.setEGLContextFactory(EGL10ContextFactory())
        glSurface.setEGLContextClientVersion(2)
        glSurface.setRenderer(presenter.renderer)

        presenter.renderer.start()

        tvStatus = vRoot.findViewById(R.id.tv_status)

        vRoot.findViewById<View>(R.id.btn_start).setOnClickListener {
            presenter.startGame()

            //it.findNavController().navigate(GameFragmentDirections.actionGameFragmentToScoreFragment())

//            presenter.ru2!!.withLocation(-1f, 1f)
//            presenter.ru2!!.addAnimation(Alpha(1f, 0f, 500, interpolator = LinearInterpolator()))
//            presenter.ru2!!.addAnimation(TranslateTo(PointF(0f, 0f), 0.0005f, interpolator =  BounceInterpolator()))
//            presenter.ru2!!.addAnimation(RotateBy(900f, 0.1f, interpolator = BounceInterpolator()))

//            presenter.ru2!!.withLocation(0f, 0f)
//            presenter.ru2!!.addAnimation(ScaleXY(1f, 2f, 1f, 2f, 4000, interpolator = LinearInterpolator())
//                .withPivot(ScaleXY.Pivot.CenterRight)
//                .withAffectChilds())

//            presenter.ru2!!.withLocation(0f, 0f)
//            presenter.ru2!!.addAnimation(Alpha(0f, 1f, 2000, interpolator = LinearInterpolator()))
        }

        vRoot.findViewById<View>(R.id.btn_stop).setOnClickListener {
            presenter.stopGame()
        }

        return vRoot
    }

    override val surfaceView get() = glSurface
    override fun requestRender() { vRoot.post { glSurface.requestRender() } }
    override fun setRenderMode(mode: Int) { vRoot.post { glSurface.renderMode = mode } }

    override fun onSceneConfigured(params: SceneParams) {
        if (sceneConfigured)
            return

        vRoot.post {
            var lp = vgCanvasGroup.layoutParams as ConstraintLayout.LayoutParams
            lp.width = params.scaledSceneWidth.toInt() //+ canvasFramePadding
            lp.height = params.scaledSceneHeight.toInt() //+ canvasFramePadding
            vgCanvasGroup.layoutParams = lp

            var vgPanel_vMid = vgPanelBottomGroup.findViewById<View>(R.id.v_mid)
            lp = vgPanel_vMid.layoutParams as ConstraintLayout.LayoutParams
            lp.width = ((2f * presenter.gameConfig.blockGapHPx + presenter.gameConfig.blockWidthPx) / params.scaleInv).toInt()
            vgPanel_vMid.layoutParams = lp

            var vgPanel_vTop = vgPanelBottomGroup.findViewById<View>(R.id.v_top)
            lp = vgPanel_vTop.layoutParams as ConstraintLayout.LayoutParams
            lp.height = (presenter.gameConfig.blockCurrGapTopPx / params.scaleInv).toInt()
            vgPanel_vTop.layoutParams = lp

            var vgPanel_vBottom = vgPanelBottomGroup.findViewById<View>(R.id.v_bottom)
            lp = vgPanel_vBottom.layoutParams as ConstraintLayout.LayoutParams
            lp.height = (presenter.gameConfig.blockCurrGapBottomPx / params.scaleInv).toInt()
            vgPanel_vBottom.layoutParams = lp

            lp = vgPanelBottomGroup.layoutParams as ConstraintLayout.LayoutParams
            lp.height = ((presenter.gameConfig.blockHeightPx + presenter.gameConfig.blockGapVPx + presenter.gameConfig.blockGapVPx + presenter.gameConfig.blockCurrGapTopPx + presenter.gameConfig.blockCurrGapBottomPx) / params.scaleInv).toInt()
            vgPanelBottomGroup.layoutParams = lp
        }
        sceneConfigured = true
    }

    override fun onCurrentBlockChanged(type: BlockType?) {
        vRoot.post {
            tvStatus.text = "NEXT_BLOCK: ${type?.id}"
        }
    }
}