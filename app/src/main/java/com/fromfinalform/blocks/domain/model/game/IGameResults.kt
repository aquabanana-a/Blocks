/*
 * Created by S.Dobranos on 05.02.21 20:01
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import android.os.Parcelable
import com.fromfinalform.blocks.common.ICloneable

interface IGameResults : ICloneable<IGameResults>, Parcelable {
    val level: Int
    val speed: Float
    val points: Long
    val completedRowsCount: Int

    fun updateBy(level: IGameLevel): IGameResults

    fun addPoints(value: Long)
    fun addCompletedRows(value: Int)
}