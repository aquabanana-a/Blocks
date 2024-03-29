package com.fromfinalform.blocks.data.repository

import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockType
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._2
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._4
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._8
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._16
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._32
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._64
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._128
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._256
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._512
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._1024
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId._2048
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import kotlin.random.Random

class ClassicBlockTypeRepository : IBlockTypeRepository {

    val rnd = Random(System.currentTimeMillis())
    val rndChancedList = arrayListOf<BlockTypeId>()

    private val blocksByTypeId = hashMapOf<BlockTypeId, BlockType>()
    override fun get(typeId: BlockTypeId): BlockType {
        return blocksByTypeId[typeId] ?: throw IllegalArgumentException()
    }

    private fun add(type: BlockType) {
        blocksByTypeId[type.id] = type
    }

    override fun initialize() {
        add(BlockType(_2, 0xFF757575, 0xFF7F7F7F, 0xFFFFFFFF, 0xFF595959,      chanceCoeff = 3))
        add(BlockType(_4, 0xFFE0B4B5, 0xFFEABEBF, 0xFF000000, 0xFFC49FA0,      chanceCoeff = 3))
        add(BlockType(_8, 0xFFB77775, 0xFFC17D7C, 0xFF000000, 0xFF9B6463,      chanceCoeff = 3))
        add(BlockType(_16, 0xFF774965, 0xFF82506F, 0xFFFFFFFF, 0xFF5B384E,     chanceCoeff = 2))
        add(BlockType(_32, 0xFFE8B077, 0xFFF2B87D, 0xFF000000, 0xFFCC9B6A,     chanceCoeff = 2))
        add(BlockType(_64, 0xFF886C44, 0xFF937549, 0xFFFFFFFF, 0xFF6D5636,     chanceCoeff = 2))
        add(BlockType(_128, 0xFFC4B07B, 0xFFCEB882, 0xFF000000, 0xFFA8966A,    chanceCoeff = 1))
        add(BlockType(_256, 0xFF9F57AF, 0xFFA95DBA, 0xFFFFFFFF, 0xFF864993,    chanceCoeff = 1))
        add(BlockType(_512, 0xFF7163CC, 0xFF776AD8, 0xFFFFFFFF, 0xFF6257B2,    chanceCoeff = 0))
        add(BlockType(_1024, 0xFF62AD4C, 0xFF68B750, 0xFF000000, 0xFF52913F,   chanceCoeff = 0))
        add(BlockType(_2048, 0xFF4995AA, 0xFF4DA0B5, 0xFFFFFFFF, 0xFF3D7E8E,   chanceCoeff = 0))

        blocksByTypeId.values.forEach {
            for (i in 0 until it.chanceCoeff)
                rndChancedList.add(if (rndChancedList.size > 0) rnd.nextInt(rndChancedList.size) else 0, it.id)
        }
        rndChancedList.shuffle()
    }

    override fun getRandom(exclude: List<BlockTypeId>?): BlockType {
        if (exclude?.containsAll(blocksByTypeId.keys) == true)
            throw IllegalArgumentException()

        val index = (rnd.nextInt(0, rndChancedList.size * 1000) / 1000f).toInt().coerceIn(0, rndChancedList.size-1)
        val bti = rndChancedList[index]
        if (exclude?.contains(bti) == true)
            return getRandom(exclude)

        return get(bti)
    }
}