/*
 * Created by S.Dobranos on 07.02.21 22:27
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game

import kotlin.math.max

class GameObjectIndexer {
    companion object {
        private var lo = Any()
        private var indexImpl = 0L

        fun getNext(): Long { synchronized(lo) {
            indexImpl = max(0L, indexImpl++)
            return indexImpl
        } }
    }
}