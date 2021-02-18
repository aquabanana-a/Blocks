/*
 * Created by S.Dobranos on 10.02.21 22:18
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.renderer.unit

import com.fromfinalform.blocks.common.ICloneable

interface IRenderItem : ICloneable<IRenderItem> {
    val id: Long
    var parent: IRenderItem?
    var childs: List<IRenderItem>?

    fun addChild(value: IRenderItem)
    fun removeChild(id: Long): IRenderItem?
}