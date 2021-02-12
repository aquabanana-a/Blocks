package com.fromfinalform.blocks.presentation.model.graphics.drawer

import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.view.Gravity
import com.fromfinalform.blocks.common.isSame
import com.fromfinalform.blocks.presentation.model.graphics.common.rotateMesh
import com.fromfinalform.blocks.presentation.model.graphics.opengl.common.GLUtils
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLColor
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLTextureRegion
import com.fromfinalform.blocks.presentation.model.graphics.renderer.data.GLVertices
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams
import io.instories.core.render.resolver.GLTextResolver
import kotlin.math.min

class TextShaderDrawer : IShaderDrawer, ISpriteDrawer {
    companion object {
        const val MARKER_R_USE_TEXTURE_COLORS = -0.1f
        const val VERTEX_SIZE           = 2 + 4 + 2     // Vertex Size (in Components) ie. (xy + rgba + uv)
        const val VERTICES_PER_SPRITE   = 4             // Vertices Per Sprite
        const val INDICES_PER_SPRITE    = 6             // Indices Per Sprite
    }

    override val VERTEX_SHADER = """
        uniform mat4 uMVPMatrix;           
        uniform mat4 uSTMatrix;            
        attribute vec4 aColor;             
        attribute vec4 aPosition;          
        attribute vec4 aTextureCoord;      
        varying vec2 vPositionCoord;       
        varying vec2 vTextureCoord;        
        varying vec4 vColor;               
                                           
        void main() {                      
            gl_Position = aPosition;         
            vPositionCoord = aPosition.xy;   
            vTextureCoord = aTextureCoord.xy;
            vColor = aColor;                 
        }                                  
        """

    override val FRAGMENT_SHADER = """
        precision mediump float;                                                             
        varying vec2 vTextureCoord;                                                          
        varying vec2 vPositionCoord;                                                         
        varying vec4 vColor;                                                                 
        uniform sampler2D sTexture;                                                          
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
            vec4 color = texture2D(sTexture, vTextureCoord);                                   
            if(vColor.r == ${MARKER_R_USE_TEXTURE_COLORS})                                     
                gl_FragColor = color * vColor.a;                                                 
            else                                                                               
                gl_FragColor = vColor * color.a;                                                 
            if(uClip[4].x > 0.0)                                                               
                gl_FragColor *= pointInRhombus(vPositionCoord, uClip);                           
        }                                                                                    
        """

    override val typeId get() = ShaderDrawerTypeId.TEXT
    override var program = 0; private set

    private var innerGravity: Int = Gravity.CENTER
    private var vertices: GLVertices
    private var vertexBuffer: FloatArray
    private var bufferIndex = 0
    private var maxSprites = 77
    private var numSprites = 0

    private var clipBuffer: FloatArray

    private var params:SceneParams? = null
    private var angle       = 0f
    private var pivot       = PointF()

    private var positionHandle = -1
    private var textureHandle = -1
    private var colorHandle = -1
    private var clipHandle  = -1

    private var clipRect: RectF? = null
    private var clipRectPrev: RectF? = null

    private var alpha = 1f

    private var textResolver: GLTextResolver? = null

    constructor() {
        program = GLUtils.createShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        if (program == 0)
            throw RuntimeException("failed creating program")

        colorHandle     = GLES20.glGetAttribLocation(program, "aColor")
        positionHandle  = GLES20.glGetAttribLocation(program, "aPosition")
        textureHandle   = GLES20.glGetAttribLocation(program, "aTextureCoord")

        clipHandle      = GLES20.glGetUniformLocation(program, "uClip")

        this.vertices       = GLVertices(maxSprites * VERTICES_PER_SPRITE, maxSprites * INDICES_PER_SPRITE, positionHandle, textureHandle, colorHandle)
        this.vertexBuffer   = FloatArray(maxSprites * VERTICES_PER_SPRITE * VERTEX_SIZE)
        this.clipBuffer     = FloatArray(10) { 0f }

        var indices = ShortArray(maxSprites * INDICES_PER_SPRITE)
        var i: Short = 0
        var v: Short = 0
        while(i < indices.size) {
            indices[i + 0] = (v + 0).toShort()
            indices[i + 1] = (v + 1).toShort()
            indices[i + 2] = (v + 2).toShort()
            indices[i + 3] = (v + 2).toShort()
            indices[i + 4] = (v + 3).toShort()
            indices[i + 5] = (v + 0).toShort()

            i = (i + INDICES_PER_SPRITE).toShort()
            v = (v + VERTICES_PER_SPRITE).toShort()
        }
        vertices.setIndices(indices, 0, indices.size)
    }

    // fun setUniforms(ru: IRenderUnit?, animation: GlAnimation?, params: SceneParams, value: Float) : ShaderProgram {
    override fun setUniforms(vararg args: Any) {
        if (args.size != 1)
            return

        this.textResolver = (args[0] as? GLTextResolver)?.withTextDrawer(this)
    }

    override fun cleanUniforms() {
        this.params = null

        angle = 0f

        clipRect = null
        clipRectPrev = null
        clipBuffer = FloatArray(10) { 0f }

        alpha = 1f

        //textResolver?.clearTransformer()
        textResolver?.withTextDrawer(null)
        textResolver = null
    }

    private fun fillClipBuffer(r: RectF?) {
        if(clipRectPrev.isSame(r))
            return

        // usage
        // [8] - mark for shader to use
        // [9] - mark for code that actual values passed through uniforms into shader
        this.clipBuffer[8] = if (r != null) 1.0f else 0.0f
        this.clipBuffer[9] = 0.0f

        clipRectPrev = r
        if(r == null) {
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

    private fun beginBatch() {
        numSprites = 0
        bufferIndex = 0
    }

    private fun endBatch() {
        if(numSprites <= 0)
            return

        fillClipBuffer(clipRect)
        val recalcClip = clipBuffer[9] < 1.0f

        if (angle % 360 != 0f) {
            if (recalcClip) rotateMesh(clipBuffer, 0, 8, angle, pivot, params!!.sceneWH, 2)
            rotateMesh(vertexBuffer, 0, bufferIndex, angle, pivot, params!!.sceneWH, VERTEX_SIZE)
        }

        if(recalcClip) {
            GLES20.glUniform2fv(clipHandle, 5, clipBuffer, 0)
            clipBuffer[9] = 1.0f
        }

        vertices.setVertices(vertexBuffer, 0, bufferIndex)
        vertices.bind()
        vertices.draw(GLES20.GL_TRIANGLES, 0, numSprites * INDICES_PER_SPRITE)
        vertices.unbind()
    }

    override fun drawSprite(x: Float, y: Float, width: Float, height: Float, region: GLTextureRegion, colors: Array<GLColor>, clip: RectF?, useTextureColors:Boolean, endBatch: Boolean) {
        val halfWidth = width / 2.0f
        val halfHeight = height / 2.0f

        val x1 = x - halfWidth      // Left X
        val y1 = y - halfHeight     // Bottom Y
        val x2 = x + halfWidth      // Right X
        val y2 = y + halfHeight     // Top Y

        drawSprite(arrayOf(PointF(x1, y1), PointF(x2, y1), PointF(x2, y2), PointF(x1, y2)), region, colors, clip, useTextureColors, endBatch)
    }

    override fun drawSprite(vertices: Array<PointF>, region: GLTextureRegion, _colors: Array<GLColor>, clip: RectF?, useTextureColors: Boolean, endBatch: Boolean) {
        if (bufferIndex >= maxSprites) {
            endBatch()
            numSprites = 0
            bufferIndex = 0
        }

        this.clipRect = clip

        val colors = _colors.map { it.clone().mulBy(min(it.getA(), alpha)) }

        vertexBuffer[bufferIndex++] = vertices[0].x
        vertexBuffer[bufferIndex++] = vertices[0].y
        vertexBuffer[bufferIndex++] = if (useTextureColors) MARKER_R_USE_TEXTURE_COLORS else colors[0].getR()
        vertexBuffer[bufferIndex++] = colors[0].getG()
        vertexBuffer[bufferIndex++] = colors[0].getB()
        vertexBuffer[bufferIndex++] = colors[0].getA()
        vertexBuffer[bufferIndex++] = region.u1
        vertexBuffer[bufferIndex++] = region.v2

        vertexBuffer[bufferIndex++] = vertices[1].x
        vertexBuffer[bufferIndex++] = vertices[1].y
        vertexBuffer[bufferIndex++] = if (useTextureColors) MARKER_R_USE_TEXTURE_COLORS else colors[1].getR()
        vertexBuffer[bufferIndex++] = colors[1].getG()
        vertexBuffer[bufferIndex++] = colors[1].getB()
        vertexBuffer[bufferIndex++] = colors[1].getA()
        vertexBuffer[bufferIndex++] = region.u2
        vertexBuffer[bufferIndex++] = region.v2

        vertexBuffer[bufferIndex++] = vertices[2].x
        vertexBuffer[bufferIndex++] = vertices[2].y
        vertexBuffer[bufferIndex++] = if (useTextureColors) MARKER_R_USE_TEXTURE_COLORS else colors[2].getR()
        vertexBuffer[bufferIndex++] = colors[2].getG()
        vertexBuffer[bufferIndex++] = colors[2].getB()
        vertexBuffer[bufferIndex++] = colors[2].getA()
        vertexBuffer[bufferIndex++] = region.u2
        vertexBuffer[bufferIndex++] = region.v1

        vertexBuffer[bufferIndex++] = vertices[3].x
        vertexBuffer[bufferIndex++] = vertices[3].y
        vertexBuffer[bufferIndex++] = if (useTextureColors) MARKER_R_USE_TEXTURE_COLORS else colors[3].getR()
        vertexBuffer[bufferIndex++] = colors[3].getG()
        vertexBuffer[bufferIndex++] = colors[3].getB()
        vertexBuffer[bufferIndex++] = colors[3].getA()
        vertexBuffer[bufferIndex++] = region.u1
        vertexBuffer[bufferIndex++] = region.v1

        numSprites++

        if (endBatch) {
            endBatch()
            numSprites = 0
            bufferIndex = 0
        }
    }

    override fun draw(ru: IRenderUnit, sceneParams: SceneParams, itemParams: ItemParams) {
        if (textResolver == null)
            return

        this.params = params
        this.angle = angle
        this.pivot = itemParams.dstPivot

        GLES20.glUseProgram(program)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textResolver!!.glTextTexture.textureId)

        beginBatch()
        textResolver!!.drawText(itemParams)
        endBatch()
    }
}