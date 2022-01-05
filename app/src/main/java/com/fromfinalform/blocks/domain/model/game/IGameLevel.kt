package com.fromfinalform.blocks.domain.model.game

import com.fromfinalform.blocks.common.ICloneable

interface IGameLevel : ICloneable<IGameLevel> {
    companion object {
        const val LVL_N = Int.MAX_VALUE
    }

    var level: Int
    val speed: Float

    val ptsSingle: Long
    val ptsDouble: Long
    val ptsTriple: Long
    val ptsTetris: Long

    val ptsPerfectClear: Long
    val ptsPerfectClearMultiplier: Float

    val ptsSoftDrop: Long
    val ptsHardDrop: Long

    fun getCompletePoints(rowsCount: Int, perfectClear: Boolean, lvl: Int = level): Long
    fun getDropPoints(softCount: Int, hardCount: Int, perfectClear: Boolean, lvl: Int = level): Long
}