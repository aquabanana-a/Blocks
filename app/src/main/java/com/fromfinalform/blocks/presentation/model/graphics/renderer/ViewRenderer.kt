/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer

import android.opengl.GLES20.glViewport
import android.util.Log
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderItem
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.RenderUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

class ViewRenderer(clearColor: Long, override var sceneSize: ISize) : IRenderer {

    val job = SupervisorJob()
    override val scope = CoroutineScope(Dispatchers.Default/*Executors.newFixedThreadPool(1).asCoroutineDispatcher()*/ + job)

    private val renderUnitsLo = Any()
    private var renderUnitsImpl = hashMapOf<Long, IRenderUnit>()
    private var renderUnitsSorted = arrayListOf<IRenderUnit>()
    override val renderUnits get() = ArrayList(renderUnitsSorted)
    override fun add(ru: IRenderUnit) { synchronized(renderUnitsLo) {
        renderUnitsImpl[ru.id] = ru
        renderUnitsSorted.add(ru)
    } }
    override fun add(rus: List<IRenderUnit>) { synchronized(renderUnitsLo) {
        rus.forEach {
            renderUnitsImpl[it.id] = it
            renderUnitsSorted.add(it)
        }
    } }

    private fun getRenderUnitImpl(list: List<IRenderItem>?, id: Long): IRenderUnit? {
        list?.forEach {
            if (it.id == id)
                return it as? RenderUnit
            else if (it is RenderItem && it.childs != null) {
                val ru = getRenderUnitImpl(it.childs, id)
                if (ru != null)
                    return ru
            }
        }
        return null
    }
    override fun getRenderUnit(id: Long): IRenderUnit? { synchronized(renderUnitsLo) {
        val ru = renderUnitsImpl[id]
        return ru ?: getRenderUnitImpl(renderUnitsSorted, id)
    } }
    override fun removeRenderUnit(id: Long): IRenderUnit? { synchronized(renderUnitsLo) {
        var ru = renderUnitsImpl.remove(id)
        if (ru != null) {
            renderUnitsSorted.remove(ru)
            return ru
        }
        else {
            ru = getRenderUnit(id)
            return ru?.parent?.removeChild(id) as? IRenderUnit
        }

        return null
    } }
    override fun clearRenderUnits() { synchronized(renderUnitsLo) {
        renderUnitsImpl.clear()
        renderUnitsSorted.clear()
    } }

    private var frames = 0L
    private var startTime = 0L
    private var lastFrameTimeMs = 0L
    private var bgARGB = GLColor(clearColor).toFloatArray() // "#007099"

    private var handler: RendererListener? = null
    private var updater: (()-> Unit)? = null

    private val sceneParams = SceneParams(sceneSize, 1f, 1f, 1f)

    private var isStarted = false
    private var isStopRequested = false

    override var renderTimeMs: Long = 0L; private set

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        configureScene(w, h)
        glViewport(0, 0, w, h)

        handler?.onSceneConfigured(sceneParams)
    }

    override fun onDrawFrame(gl: GL10?) { try {
        if (isStarted && frames == 0L)
            handler?.onFirstFrame()

        ++frames

        val nowTimeMs = System.currentTimeMillis()
        renderTimeMs = nowTimeMs - startTime
        val deltaTimeMs = if (lastFrameTimeMs <= 0) 0 else max(0, nowTimeMs - lastFrameTimeMs)
        var renderParams = RenderParams(frames, renderTimeMs, deltaTimeMs)

        GLUtils.clear(bgARGB)

        if (isStarted) {
            if (isStopRequested) {
                stopImpl()
                handler?.onStop()
            }
            else {
                handler?.onFrame(renderParams, sceneParams)
            }

            synchronized(renderUnitsLo) {
                for (ru in renderUnitsSorted)
                    ru.render(this, renderParams, sceneParams)
            }
        }

        lastFrameTimeMs = nowTimeMs

        } catch (e: Exception) {
            Log.v("!", "crash");
            handler?.onCrash()
            stop()
        }
    }

    private fun configureScene(canvasWidth: Int, canvasHeight: Int) {
        var scale = min(canvasWidth / sceneSize.width, canvasHeight / sceneSize.height)
        sceneParams.update(sceneSize, scale, sceneSize.width * scale, sceneSize.height * scale)
    }

    override fun requestRender() {
        updater?.invoke()
    }

    override fun start() {
        if (isStarted)
            return

        startTime = System.currentTimeMillis()
        lastFrameTimeMs = 0
        frames = 0
        isStopRequested = false
        isStarted = true

        handler?.onStart()

        synchronized(renderUnitsLo) {
            for (ru in renderUnitsSorted)
                ru.prerender(this)
        }
    }

    override fun stop() {
        if (isStarted)
            isStopRequested = true
    }

    private fun stopImpl() {
        isStarted = false
        isStopRequested = false
        frames = 0

        synchronized(renderUnitsLo) {
            for (ru in renderUnitsSorted)
                ru.postrender(this)
        }
    }

    override fun withListener(handler: RendererListener): ViewRenderer {
        this.handler = handler
        return this
    }

    override fun withUpdater(handler: ()->Unit): ViewRenderer {
        this.updater = handler
        return this
    }
}