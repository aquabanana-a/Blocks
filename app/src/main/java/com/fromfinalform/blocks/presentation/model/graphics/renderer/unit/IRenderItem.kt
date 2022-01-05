package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import com.fromfinalform.blocks.common.ICloneable

interface IRenderItem : ICloneable<IRenderItem> {
    val id: Long
    val itemParams: ItemParams

    var parent: IRenderItem?
    var childs: List<IRenderItem>?

    fun addChild(value: IRenderItem)
    fun removeChild(id: Long): IRenderItem?
}