/*
 * Created by S.Dobranos on 05.02.21 20:15
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.`object`.block

enum class BlockTypeId(val id: Int) {
    _2      (1)     { override fun toString() = "2" },
    _4      (2)     { override fun toString() = "4" },
    _8      (3)     { override fun toString() = "8" },
    _16     (4)     { override fun toString() = "16" },
    _32     (5)     { override fun toString() = "32" },
    _64     (6)     { override fun toString() = "64" },
    _128    (7)     { override fun toString() = "128" },
    _256    (8)     { override fun toString() = "256" },
    _512    (9)     { override fun toString() = "512" },
    _1024   (10)    { override fun toString() = "1024" },
    _2048   (11)    { override fun toString() = "2048" };

    fun hasNext(): Boolean = (ordinal + 1) in 0..values().size

    fun getNext():BlockTypeId? = if (!hasNext()) null else values()[ordinal + 1]
}