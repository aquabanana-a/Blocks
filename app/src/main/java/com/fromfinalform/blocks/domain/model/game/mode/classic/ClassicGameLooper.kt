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
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class ClassicGameLooper : IGameLooper {

    @Inject constructor()
    @Inject lateinit var config: IGameConfig
    @Inject lateinit var field: IGameField
    @Inject lateinit var blockTypeRepo: IBlockTypeRepository

    private val objectsLo = Any()

    override val objects: List<GameObject> get() { synchronized(objectsLo) {
        val cb = currentBlock.value
        return if (cb != null) this.field.objects + cb else this.field.objects.toList()
    } }
    override val objectsDirtyFlat: List<GameObject> get() = getDirtyFlatList(objects)

    override lateinit var currentBlock: BehaviorSubject<Block?>; private set

    private var objectsCountChanged: ((List<GameObject>?, List<GameObject>?)->Unit)? = null
    override fun withObjectsCountChangedListener(handler: ((List<GameObject>?, List<GameObject>?)->Unit)?): ClassicGameLooper {
        this.objectsCountChanged = handler
        return this
    }

    private fun genNextBlock() { synchronized(objectsLo) {
        currentBlock.onNext(BlockBuilder(config, blockTypeRepo)
            .withRandomTypeId(currentBlock.value?.typeId)
            .build()
            .withLocation((config.fieldWidthPx - config.blockWidthPx)/2f, config.fieldHeightPx + config.blockCurrGapTopPx + config.blockGapVPx + config.blockGapVPx) as Block)
    } }

    override fun init() {
        field.init()
        currentBlock = BehaviorSubject.create()

        genNextBlock()

        field.withColumnTouchdownListener { index, location ->

            field.placeTo(currentBlock.value!!.clone(), index)
            genNextBlock()

            true
        }
    }

    override fun start() {
        field.clear()

    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams) {
        field.onFrameDrawn(renderParams, sceneParams)
    }

    override fun onTouch(me: MotionEvent, sp: SceneParams): Boolean {
        return field.onTouch(me, sp)
    }

    private fun getDirtyFlatList(list: List<GameObject>?): List<GameObject> {
        val ret = arrayListOf<GameObject>()
        list?.forEach {
            if (it.isDirty) ret.add(it)
            val sl = getDirtyFlatList(it.childs)
            if (sl.isNotEmpty()) ret.addAll(sl)
        }
        return ret
    }
}