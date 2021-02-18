/*
 * Created by S.Dobranos on 16.02.21 21:19
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.mode.classic

import android.view.MotionEvent
import com.fromfinalform.blocks.domain.interactor.BlockBuilder
import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.`object`.block.BlockTypeId
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import javax.inject.Inject

class ClassicGameLooper : IGameLooper {

    @Inject constructor()
    @Inject lateinit var config: IGameConfig
    @Inject lateinit var field: IGameField

    override val objects: List<GameObject> get() = this.field.objects// + nextBlock
    override val objectsDirtyFlat: List<GameObject> get() = getDirtyFlatList(objects)

    override var nextBlock: Block? = null; private set

    private var objectsCountChanged: ((List<GameObject>?, List<GameObject>?)->Unit)? = null
    override fun withObjectsCountChangedListener(handler: ((List<GameObject>?, List<GameObject>?)->Unit)?): ClassicGameLooper {
        this.objectsCountChanged = handler
        return this
    }

    override fun init() {
        field.init()
    }

    override fun start() {
        var go = BlockBuilder(config)
            .withTypeId(BlockTypeId._512)
            .build()

        objectsCountChanged?.invoke(arrayListOf(go), null)
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun onTouch(me: MotionEvent, sp: SceneParams): Boolean {
        return field.onTouch(me, sp)
    }

    private fun getDirtyFlatList(list: List<GameObject>?): List<GameObject> {
        val ret = arrayListOf<GameObject>()
        list?.forEach {
            if (it.isDirty) ret.add(it)
            ret.addAll(getDirtyFlatList(it.childs))
        }
        return ret
    }
}