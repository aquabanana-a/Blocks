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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.fromfinalform.blocks.R
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
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)

        vRoot.findViewById<View>(R.id.btn_start).setOnClickListener {
            //it.findNavController().navigate(GameFragmentDirections.actionGameFragmentToScoreFragment())
            presenter.renderer.start()
        }

        vRoot.findViewById<View>(R.id.btn_stop).setOnClickListener {
            presenter.renderer.stop()
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