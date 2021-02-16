/*
 * Created by S.Dobranos on 16.02.21 21:21
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`

import kotlin.math.max

class GameObjectIndexer {
    companion object {
        private var lo = Any()
        private var indexImpl = 0L

        fun getNext(): Long { synchronized(lo) {
            indexImpl = max(0L, indexImpl + 1)
            return indexImpl
        } }
    }
}