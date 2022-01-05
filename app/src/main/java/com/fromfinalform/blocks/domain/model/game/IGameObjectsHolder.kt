package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.domain.model.game.`object`.GameObject

interface IGameObjectsHolder {
    val objects: List<GameObject>

}