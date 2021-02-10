/*
 * Created by S.Dobranos on 07.02.21 21:20
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

interface IGameFieldBackground {

    fun build(config: IGameConfig): GameObject
}