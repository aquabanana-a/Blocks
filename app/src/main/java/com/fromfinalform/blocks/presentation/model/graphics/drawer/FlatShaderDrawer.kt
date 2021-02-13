/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.drawer

import android.graphics.RectF
import android.opengl.GLES20
import com.fromfinalform.blocks.presentation.model.graphics.common.rotateMesh
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLVertices
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams

class FlatShaderDrawer() : IShaderDrawer {
    companion object {
        const val VERTEX_SIZE = 2 + 2
        const val VERTICES_PER_SPRITE = 4
        const val INDICES_PER_SPRITE = 6
    }

    override val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        attribute vec4 aTextureCoord;
        varying vec2 vTextureCoord;
        
        void main() {
            gl_Position = aPosition;
            vTextureCoord = (aTextureCoord).xy;
        }
        """

    override val FRAGMENT_SHADER = """
        /*precision mediump float;*/
        precision highp float;
        varying vec2 vTextureCoord;
        uniform sampler2D sTexture;
        
        void main() {
            gl_FragColor = texture2D(sTexture, vTextureCoord);
        }
        """

    override val typeId get() = ShaderDrawerTypeId.FLAT
    override var program = 0; private set

    private var vertices: GLVertices
    private var vertexBuffer: FloatArray
    private var bufferIndex = 0

    private var positionHandle = -1
    private var textureHandle = -1

    var textureId: Int = -1; private set

    init {
        program = GLUtils.createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        if (program == 0)
            throw RuntimeException("failed creating program")

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        textureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord")

        this.vertices = GLVertices(VERTICES_PER_SPRITE, INDICES_PER_SPRITE, positionHandle, textureHandle, -1)
        this.vertexBuffer = FloatArray(VERTICES_PER_SPRITE * VERTEX_SIZE)

        var indices = ShortArray(GradientShaderDrawer.INDICES_PER_SPRITE)
        var i: Short = 0
        var v: Short = 0
        while (i < indices.size) {
            indices[i + 0] = (v + 0).toShort()
            indices[i + 1] = (v + 1).toShort()
            indices[i + 2] = (v + 2).toShort()
            indices[i + 3] = (v + 2).toShort()
            indices[i + 4] = (v + 3).toShort()
            indices[i + 5] = (v + 1).toShort()

            i = (i + GradientShaderDrawer.INDICES_PER_SPRITE).toShort()
            v = (v + GradientShaderDrawer.VERTICES_PER_SPRITE).toShort()
        }
        vertices.setIndices(indices, 0, indices.size)
    }

    private fun refreshMesh(dst: RectF, src: RectF, params: SceneParams) {
        bufferIndex = 0
        vertexBuffer[bufferIndex++] = dst.left
        vertexBuffer[bufferIndex++] = dst.top
        vertexBuffer[bufferIndex++] = src.left
        vertexBuffer[bufferIndex++] = src.top

        vertexBuffer[bufferIndex++] = dst.left
        vertexBuffer[bufferIndex++] = dst.bottom
        vertexBuffer[bufferIndex++] = src.left
        vertexBuffer[bufferIndex++] = src.bottom

        vertexBuffer[bufferIndex++] = dst.right
        vertexBuffer[bufferIndex++] = dst.top
        vertexBuffer[bufferIndex++] = src.right
        vertexBuffer[bufferIndex++] = src.top

        vertexBuffer[bufferIndex++] = dst.right
        vertexBuffer[bufferIndex++] = dst.bottom
        vertexBuffer[bufferIndex++] = src.right
        vertexBuffer[bufferIndex++] = src.bottom
    }

    override fun setUniforms(vararg args: Any) {
        if (args.size != 1)
            return

        this.textureId = (args[0] as? Int) ?: -1
    }

    override fun cleanUniforms() {
        this.textureId = -1
    }

    private fun rotateBy(itemParams: ItemParams?, sceneParams: SceneParams) {
        if (itemParams == null)
            return

        if (itemParams.angle % 360 != 0f)
            rotateMesh(vertexBuffer, 0, bufferIndex, itemParams.angle, itemParams.anglePivot, sceneParams.sceneWH, SolidShaderDrawer.VERTEX_SIZE)
    }

    override fun draw(ru: IRenderUnit, sceneParams: SceneParams, itemParams: ItemParams, parentParams: ItemParams?) {
        if (textureId < 0)
            return

        GLES20.glUseProgram(program)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        refreshMesh(itemParams.dstRect, itemParams.srcRect, sceneParams)

        rotateBy(parentParams, sceneParams)
        rotateBy(itemParams, sceneParams)

        vertices.setVertices(vertexBuffer, 0, bufferIndex)
        vertices.bind()
        vertices.draw(GLES20.GL_TRIANGLES, 0, GradientShaderDrawer.INDICES_PER_SPRITE)
        vertices.unbind()
    }
}