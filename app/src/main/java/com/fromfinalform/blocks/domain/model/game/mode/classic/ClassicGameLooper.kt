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
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimation
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimationTypeId
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import com.fromfinalform.blocks.presentation.model.graphics.interpolator.EaseInEaseOutInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.interpolator.EaseOutInterpolator
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

class ClassicGameLooper : IGameLooper {

    @Inject constructor()
    @Inject lateinit var config: IGameConfig
    @Inject lateinit var field: IGameField
    @Inject lateinit var blockTypeRepo: IBlockTypeRepository

//    private val objectsLo = Any()
    override val objects: List<GameObject> get() { /*synchronized(objectsLo) {*/
        val cb = currentBlock.value
        val ret = this.field.objects.toMutableList()
        if (cb != null && !cb.isWaitForRemove && !cb.isRemoved)
            ret += cb
        return ret
    } /*}*/
    override val objectsDirtyFlat: List<GameObject> get() = getDirtyFlatList(objects)

    override lateinit var trashBlock: BehaviorSubject<Block?>; private set
    override lateinit var currentBlock: BehaviorSubject<Block?>; private set
    private var currentBlockProto: Block? = null

    private fun genNextBlock() { /*synchronized(objectsLo) {*/
        currentBlockProto = BlockBuilder(config, blockTypeRepo)
            .withRandomTypeId(currentBlock.value?.typeId)
            .build()
        currentBlock.onNext(currentBlockProto!!.clone()
            .withLocation((config.fieldWidthPx - config.blockWidthPx)/2f, config.fieldHeightPx + config.blockCurrGapTopPx + config.blockGapVPx + config.blockGapVPx)
            .requestAnimation(GameObjectAnimation(GameObjectAnimationTypeId.SCALE)
                .withParam(GameObjectAnimation.PARAM_TIMELINE_ID, 0)
                .withParam(GameObjectAnimation.PARAM_SCALE_FROM, 0.001f)
                .withParam(GameObjectAnimation.PARAM_SCALE_TO, 1.0f)
                .withParam(GameObjectAnimation.PARAM_DURATION, 800L)
                .withParam(GameObjectAnimation.PARAM_INTERPOLATOR, EaseInEaseOutInterpolator())
                .withParam(GameObjectAnimation.PARAM_AFFECT_CHILDS))
            .requestAnimation(GameObjectAnimation(GameObjectAnimationTypeId.ROTATE)
                .withParam(GameObjectAnimation.PARAM_TIMELINE_ID, 1)
                .withParam(GameObjectAnimation.PARAM_DEST_ANGLE, 360f * 4)
                .withParam(GameObjectAnimation.PARAM_SPEED, 2.0f)
                .withParam(GameObjectAnimation.PARAM_INTERPOLATOR, EaseOutInterpolator()))
            as Block)
    } /*}*/

    override fun init() {
        field.init()
        currentBlock = BehaviorSubject.create()
        trashBlock = BehaviorSubject.create()

        genNextBlock()

        field.withColumnTouchdownListener { index, location -> /*synchronized(objectsLo) {*/
            var nb = currentBlock.value
            if (nb == null || nb.isWaitForRemove || nb.isRemoved)
                return@withColumnTouchdownListener /*@synchronized*/ false

            field.placeTo(currentBlockProto!!, index)
            trashBlock.onNext(nb.requestRemove() as Block)
            genNextBlock()

            return@withColumnTouchdownListener /*@synchronized*/ true
        } /*}*/
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