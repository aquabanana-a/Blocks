/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.drawer

import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import com.fromfinalform.blocks.common.clone
import com.fromfinalform.blocks.common.heightInv
import com.fromfinalform.blocks.presentation.model.graphics.common.rotateMesh
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils.createShaderProgram
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLGradient
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLVertices
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import kotlin.math.sqrt

class GradientShaderDrawer() : IShaderDrawer {
    companion object {
        const val VERTEX_SIZE = 2 + 4         // Vertex Size (in Components) ie. (xy + rgba)
        const val VERTICES_PER_SPRITE = 4             // Vertices Per Sprite
        const val INDICES_PER_SPRITE = 6             // Indices Per Sprite
    }

    override val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uSTMatrix;
        attribute vec4 aColor;
        attribute vec4 aPosition;
        varying vec4 vColor;
        varying vec2 vPositionCoord;

        void main() {
            gl_Position = aPosition;
            vPositionCoord = aPosition.xy;
            vColor = aColor;
        }
        """

    override val FRAGMENT_SHADER = """
        precision mediump float;
        varying vec4 vColor;
        varying vec2 vPositionCoord;
        uniform vec2 uClip[5];

        bool pointInTriangle(vec2 p, vec2 r[3]) {                                            
          float a = (r[0].x - p.x) * (r[1].y - r[0].y) - (r[1].x - r[0].x) * (r[0].y - p.y); 
          float b = (r[1].x - p.x) * (r[2].y - r[1].y) - (r[2].x - r[1].x) * (r[1].y - p.y); 
          float c = (r[2].x - p.x) * (r[0].y - r[2].y) - (r[0].x - r[2].x) * (r[2].y - p.y); 
          return a >= 0.0 && b >= 0.0 && c >= 0.0 || a <= 0.0 && b <= 0.0 && c <= 0.0;       
        }                                                                                    
                                                                                             
        float pointInRhombus(vec2 p, vec2 r[5]) {                                            
          vec2 t1[3]; t1[0] = r[0]; t1[1] = r[1]; t1[2] = r[2];                              
          vec2 t2[3]; t2[0] = r[0]; t2[1] = r[2]; t2[2] = r[3];                              
          return float(pointInTriangle(p, t1) || pointInTriangle(p, t2));                    
        }                                                                                    

        void main() {
            gl_FragColor = vColor;
            if(uClip[4].x > 0.0)
                gl_FragColor *= pointInRhombus(vPositionCoord, uClip);
        }
        """

    override val typeId get() = ShaderDrawerTypeId.GRADIENT
    override var program = 0; private set

    private var vertices: GLVertices
    private var vertexBuffer: FloatArray
    private var clipBuffer: FloatArray
    private var bufferIndex = 0

    private var positionHandle = -1
    private var colorHandle = -1
    private var clipHandle = -1

    init {
        program = createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        if (program == 0)
            throw RuntimeException("failed creating program")

        colorHandle = GLES20.glGetAttribLocation(program, "aColor")
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        clipHandle = GLES20.glGetUniformLocation(program, "uClip")

        this.vertices = GLVertices(VERTICES_PER_SPRITE, INDICES_PER_SPRITE, positionHandle, -1, colorHandle)
        this.vertexBuffer = FloatArray(VERTICES_PER_SPRITE * VERTEX_SIZE)
        this.clipBuffer = FloatArray(10) { 0f }

        var indices = ShortArray(INDICES_PER_SPRITE)
        var i: Short = 0
        var v: Short = 0
        while (i < indices.size) {
            indices[i + 0] = (v + 0).toShort()
            indices[i + 1] = (v + 1).toShort()
            indices[i + 2] = (v + 2).toShort()
            indices[i + 3] = (v + 2).toShort()
            indices[i + 4] = (v + 3).toShort()
            indices[i + 5] = (v + 1).toShort()

            i = (i + INDICES_PER_SPRITE).toShort()
            v = (v + VERTICES_PER_SPRITE).toShort()
        }
        vertices.setIndices(indices, 0, indices.size)
    }

    private fun fillClipBuffer(r: RectF?) {
//        if (clipRectPrev.isSame(r))
//            return

        // usage
        // [8] - mark for shader to use
        // [9] - mark for code that actual values passed through uniforms into shader
        this.clipBuffer[8] = if (r != null) 1.0f else 0.0f
        this.clipBuffer[9] = 0.0f

//        clipRectPrev = r
        if (r == null) {
            for (i in 0 until 8)
                this.clipBuffer[i] = 0f
            return
        }

        // A
        this.clipBuffer[0] = r.left
        this.clipBuffer[1] = r.bottom

        // B
        this.clipBuffer[2] = r.right
        this.clipBuffer[3] = r.bottom

        // C
        this.clipBuffer[4] = r.right
        this.clipBuffer[5] = r.top

        // D
        this.clipBuffer[6] = r.left
        this.clipBuffer[7] = r.top
    }

    private fun refreshMesh(dst: RectF, params: SceneParams) {
        val dw = dst.width()
        val dh = dst.heightInv()

        var dstDiag = sqrt(dw * dw + dh * dh)
        var scaleX = 0f
        var scaleY = 0f

        if (dh > dw) {
            scaleX = dstDiag / dw
            scaleY = dstDiag / dh
        } else {
            scaleX = dstDiag / dh
            scaleY = dstDiag / dw
        }

        //var dst = ru.getRectOriginDst()
        var dstScaled = RectF()
        if (dst != null) {
            var offsetX = dw * scaleX / 2
            var offsetY = dh * scaleY / 2

            dstScaled = dst.clone()

            dstScaled.left -= offsetX
            dstScaled.right += offsetX

            dstScaled.top += offsetY
            dstScaled.bottom -= offsetY
        }

        val colors = Array(4) { i ->
            if (!background.isGradient())
                background.colorPrimary
            else {
                if (i % 2 == 0)
                    background.colorPrimary
                else
                    background.colorSecondary!!
            }
        }

        bufferIndex = 0
        vertexBuffer[bufferIndex++] = dstScaled.left
        vertexBuffer[bufferIndex++] = dstScaled.top
        vertexBuffer[bufferIndex++] = colors[0].getR()
        vertexBuffer[bufferIndex++] = colors[0].getG()
        vertexBuffer[bufferIndex++] = colors[0].getB()
        vertexBuffer[bufferIndex++] = colors[0].getA()

        vertexBuffer[bufferIndex++] = dstScaled.left
        vertexBuffer[bufferIndex++] = dstScaled.bottom
        vertexBuffer[bufferIndex++] = colors[1].getR()
        vertexBuffer[bufferIndex++] = colors[1].getG()
        vertexBuffer[bufferIndex++] = colors[1].getB()
        vertexBuffer[bufferIndex++] = colors[1].getA()

        vertexBuffer[bufferIndex++] = dstScaled.right
        vertexBuffer[bufferIndex++] = dstScaled.top
        vertexBuffer[bufferIndex++] = colors[2].getR()
        vertexBuffer[bufferIndex++] = colors[2].getG()
        vertexBuffer[bufferIndex++] = colors[2].getB()
        vertexBuffer[bufferIndex++] = colors[2].getA()

        vertexBuffer[bufferIndex++] = dstScaled.right
        vertexBuffer[bufferIndex++] = dstScaled.bottom
        vertexBuffer[bufferIndex++] = colors[3].getR()
        vertexBuffer[bufferIndex++] = colors[3].getG()
        vertexBuffer[bufferIndex++] = colors[3].getB()
        vertexBuffer[bufferIndex++] = colors[3].getA()
    }

    var background: GLGradient = GLGradient.TRANSPARENT; private set

    override fun setUniforms(vararg args: Any) {
        if (args.size != 1)
            return

        this.background = (args[0] as? GLGradient) ?: GLGradient.TRANSPARENT
    }

    override fun cleanUniforms() {
        this.background = GLGradient.TRANSPARENT
        this.clipBuffer = FloatArray(10) { 0f }
    }

    override fun draw(ru: IRenderUnit, params: SceneParams, dst: RectF?, src: RectF?, angle: Float) {
        if (dst == null)
            return

        GLES20.glUseProgram(program)

        refreshMesh(dst, params)
        fillClipBuffer(dst)

        val pivot = PointF((dst.left + dst.right) / 2, (dst.bottom + dst.top) / 2)
        val bga = background.angle + angle
        if (bga % 360 != 0f) {
            rotateMesh(clipBuffer, 0, 8, angle, pivot, params.sceneWH, 2)
            rotateMesh(vertexBuffer, 0, bufferIndex, bga, pivot, params.sceneWH, VERTEX_SIZE)
        }

        GLES20.glUniform2fv(clipHandle, 5, clipBuffer, 0)

        vertices.setVertices(vertexBuffer, 0, bufferIndex)
        vertices.bind()
        vertices.draw(GLES20.GL_TRIANGLES, 0, INDICES_PER_SPRITE)
        vertices.unbind()
    }
}