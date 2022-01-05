package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig

interface IGameFieldBackground {

    fun build(config: IGameConfig): GameObject
}