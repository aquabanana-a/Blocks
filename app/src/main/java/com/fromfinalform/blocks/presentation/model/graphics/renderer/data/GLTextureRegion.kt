package com.fromfinalform.blocks.presentation.model.graphics.renderer.data

class GLTextureRegion() {
    companion object {
        val Zero = GLTextureRegion()
        val Full = GLTextureRegion(0f, 0f, 1f, 1f)
    }

    var u1 = 0f
    var v1 = 0f
    var u2 = 0f
    var v2 = 0f

    constructor(u1: Float, v1: Float, u2: Float, v2: Float): this() {
        this.u1 = u1
        this.v1 = v1
        this.u2 = u2
        this.v2 = v2
    }

    // texW, texH - the width and height of the texture the region is for
    // x, y - the top/left (x,y) of the region on the texture (in pixels)
    // w, h - the width and height of the region on the texture (in pixels)
    constructor(texW: Float, texH: Float, x: Float, y: Float, w: Float, h: Float) : this() {
        this.u1 = x / texW
        this.v1 = y / texH
        this.u2 = this.u1 + (w / texW)
        this.v2 = this.v1 + (h / texH)
    }

    fun clone(): GLTextureRegion = GLTextureRegion(u1, v1, u2, v2)
}