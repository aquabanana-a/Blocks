/*
 * Created by S.Dobranos on 07.02.21 21:20
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig

interface IGameFieldBackground {

    fun build(config: IGameConfig): GameObject
}