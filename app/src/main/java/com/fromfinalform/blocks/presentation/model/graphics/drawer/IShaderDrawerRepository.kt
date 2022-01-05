package com.fromfinalform.blocks.presentation.model.graphics.drawer

interface IShaderDrawerRepository {
    fun initialize()
    operator fun get(typeId: ShaderDrawerTypeId): IShaderDrawer?
}