/*
 * Created by S.Dobranos on 17.02.21 20:50
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject

interface IGameObjectsHolder {
    val objects: List<GameObject>

}