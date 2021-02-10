/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.model.graphics.drawer

enum class ShaderDrawerTypeId(val id: Int) {
    NONE(0),

    SOLID(1),
    FLAT(2),

    GRADIENT(10),

    TEXT(20)
}