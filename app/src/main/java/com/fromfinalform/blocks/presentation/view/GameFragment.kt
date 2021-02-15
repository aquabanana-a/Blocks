/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.view

import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.presentation.model.graphics.animation.*
import com.fromfinalform.blocks.presentation.model.graphics.interpolator.BounceInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.opengl.EGL10ContextFactory
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.presenter.GamePresenter
import com.fromfinalform.blocks.presentation.view.common.dp
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter

class GameFragment : MvpAppCompatFragment(), GamePresenter.GameView {

    private lateinit var vRoot: ConstraintLayout
    private lateinit var vgCanvasGroup: ConstraintLayout
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
        vHGap = vRoot.findViewById(R.id.v_hgap)

        glSurface = vRoot.findViewById(R.id.gl_surface)
        glSurface.setEGLContextFactory(EGL10ContextFactory())
        glSurface.setEGLContextClientVersion(2)
        glSurface.setRenderer(presenter.renderer)

        presenter.renderer.start()

        tvStatus = vRoot.findViewById(R.id.tv_status)

        vRoot.findViewById<View>(R.id.btn_start).setOnClickListener {
            //it.findNavController().navigate(GameFragmentDirections.actionGameFragmentToScoreFragment())

            presenter.ru2!!.withLocation(-1f, 1f)
            presenter.ru2!!.addAnimation(Alpha(1f, 0f, 500, interpolator = LinearInterpolator()))
            presenter.ru2!!.addAnimation(TranslateTo(PointF(0f, 0f), 0.0005f, interpolator =  BounceInterpolator()))
            presenter.ru2!!.addAnimation(RotateBy(900f, 0.1f, interpolator = BounceInterpolator()))

//            presenter.ru2!!.withLocation(0f, 0f)
//            presenter.ru2!!.addAnimation(ScaleXY(1f, 2f, 1f, 2f, 4000, interpolator = LinearInterpolator())
//                .withPivot(ScaleXY.Pivot.CenterRight)
//                .withAffectChilds())

//            presenter.ru2!!.withLocation(0f, 0f)
//            presenter.ru2!!.addAnimation(Alpha(0f, 1f, 2000, interpolator = LinearInterpolator()))
        }

        vRoot.findViewById<View>(R.id.btn_stop).setOnClickListener {

        }

        return vRoot
    }

    override fun requestRender() { vRoot.post { glSurface.requestRender() } }
    override fun setRenderMode(mode: Int) { vRoot.post { glSurface.renderMode = mode } }

    override fun onSceneConfigured(params: SceneParams) {
        if (sceneConfigured)
            return

        vRoot.post {
            val appPadding = (2 + 2).dp
            val mainFramePadding = (14 + 14).dp
            val canvasFramePadding = (5 + 5).dp

            val clp = vgCanvasGroup.layoutParams as ConstraintLayout.LayoutParams
            clp.width = params.scaledSceneWidth.toInt() + canvasFramePadding
            clp.height = params.scaledSceneHeight.toInt() + canvasFramePadding
            vgCanvasGroup.layoutParams = clp
        }
        sceneConfigured = true
    }
}