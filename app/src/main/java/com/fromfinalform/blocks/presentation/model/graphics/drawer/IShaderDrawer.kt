/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.drawer

import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.IRenderUnit
import com.fromfinalform.blocks.presentation.model.graphics.renderer.unit.ItemParams

interface IShaderDrawer {
    val VERTEX_SHADER: String
    val FRAGMENT_SHADER: String

    val typeId: ShaderDrawerTypeId
    val program: Int

    fun setUniforms(vararg args: Any)
    fun cleanUniforms()

    fun draw(ru: IRenderUnit, sceneParams: SceneParams, itemParams: ItemParams)
}