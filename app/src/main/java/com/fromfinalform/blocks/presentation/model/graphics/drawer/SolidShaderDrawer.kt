package com.fromfinalform.blocks.presentation.model.graphics.drawer

import android.graphics.RectF
import android.opengl.GLES20
import com.fromfinalform.blocks.presentation.model.graphics.common.rotateMesh
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLVertices
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams
import kotlin.math.min

class SolidShaderDrawer() : IShaderDrawer {
    companion object {
        const val VERTEX_SIZE = 2// + 4
        const val VERTICES_PER_SPRITE = 4
        const val INDICES_PER_SPRITE = 6
    }

    override val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        
        void main() {
            gl_Position = aPosition;
        }
        """

    override val FRAGMENT_SHADER = """
        precision mediump float;
        /*precision highp float;*/
        uniform vec4 uColor;
        
        void main() {
            gl_FragColor = uColor;
        }
        """

    override val typeId get() = ShaderDrawerTypeId.SOLID
    override var program = 0; private set

    private var vertices: GLVertices
    private var vertexBuffer: FloatArray
    private var bufferIndex = 0
    private var alpha = 1f

    private var positionHandle = -1
    private var colorHandle = -1

    init {
        program = GLUtils.createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        if (program == 0)
            throw RuntimeException("failed creating program")

        colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")

        this.vertices = GLVertices(VERTICES_PER_SPRITE, INDICES_PER_SPRITE, positionHandle, -1, -1)
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

    private fun refreshMesh(dst: RectF, params: SceneParams) {
        val c = color.clone().mulBy(min(color.getA(), alpha))

        bufferIndex = 0
        vertexBuffer[bufferIndex++] = dst.left
        vertexBuffer[bufferIndex++] = dst.top

        vertexBuffer[bufferIndex++] = dst.left
        vertexBuffer[bufferIndex++] = dst.bottom

        vertexBuffer[bufferIndex++] = dst.right
        vertexBuffer[bufferIndex++] = dst.top

        vertexBuffer[bufferIndex++] = dst.right
        vertexBuffer[bufferIndex++] = dst.bottom

        GLES20.glUniform4f(colorHandle, c.getR(), c.getG(), c.getB(), c.getA())
    }

    var color: GLColor = GLColor.BLACK; private set

    override fun setUniforms(vararg args: Any) {
        if (args.size != 1)
            return

        this.color = (args[0] as? GLColor) ?: GLColor.BLACK
    }

    override fun cleanUniforms() {
        this.color = GLColor.BLACK
        this.alpha = 1f
    }

    private fun rotateBy(itemParams: ItemParams?, sceneParams: SceneParams) {
        if (itemParams == null)
            return

        if (itemParams.angle % 360 != 0f)
            rotateMesh(vertexBuffer, 0, bufferIndex, itemParams.angle, itemParams.anglePivot, sceneParams.sceneWH, VERTEX_SIZE)
    }

    override fun draw(ru: IRenderUnit, sceneParams: SceneParams, itemParams: ItemParams, parentParams: ItemParams?) {
        GLES20.glUseProgram(program)

        this.alpha = itemParams.alpha
        refreshMesh(itemParams.dstRect, sceneParams)

        rotateBy(parentParams, sceneParams)
        rotateBy(itemParams, sceneParams)

        vertices.setVertices(vertexBuffer, 0, bufferIndex)
        vertices.bind()
        vertices.draw(GLES20.GL_TRIANGLES, 0, GradientShaderDrawer.INDICES_PER_SPRITE)
        vertices.unbind()
    }
}