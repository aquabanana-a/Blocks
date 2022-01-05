package com.fromfinalform.blocks.presentation.model.repository

import com.fromfinalform.blocks.presentation.model.graphics.drawer.*

class ShaderDrawerRepository : IShaderDrawerRepository {

    private val programsByTypeId = hashMapOf<ShaderDrawerTypeId, IShaderDrawer>()
    private val programsLo = Any()

    override fun get(typeId: ShaderDrawerTypeId): IShaderDrawer? { synchronized(programsLo) {
        return programsByTypeId[typeId]
    } }

    override fun initialize() { synchronized(programsLo) {
        programsByTypeId[ShaderDrawerTypeId.SOLID]      = SolidShaderDrawer()
        programsByTypeId[ShaderDrawerTypeId.FLAT]       = FlatShaderDrawer()
        programsByTypeId[ShaderDrawerTypeId.GRADIENT]   = GradientShaderDrawer()
        programsByTypeId[ShaderDrawerTypeId.TEXT]       = TextShaderDrawer()
    } }
}