/*
 * Created by S.Dobranos on 16.02.21 21:18
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.mode.classic

import android.graphics.PointF
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject.Companion.clearRemoved
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimation
import com.fromfinalform.blocks.domain.model.game.`object`.animation.GameObjectAnimationTypeId
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import javax.inject.Inject

class ClassicGameField : IGameField {

    @Inject constructor()
    @Inject lateinit var config: IGameConfig

    private lateinit var background: GameObject
    private val highlightItems = hashMapOf<Int/*column*/, GameObject>()
    private val items = ArrayList<ArrayList<GameObject>>()
    private val itemsLo = Any()

    override val objects: List<GameObject> get() = synchronized(itemsLo) { items.flatten().filterNotNull() + background }

    override fun init() { synchronized(itemsLo) {
        background = GameObject()
        for (column in 0 until config.fieldWidth) {
            val columnX = column * (config.blockWidthPx + config.blockGapHPx)

            background.add(GameObject().apply {
                x = columnX
                width = config.blockWidthPx
                height = config.fieldHeightPx
                assetId = R.drawable.bg_03
            })

            background.add(GameObject().apply {
                x = columnX
                width = config.blockWidthPx
                height = config.fieldHeightPx
                color = 0x10FFFFFF
                alpha = 0f
                tag = column
            }.also { highlightItems[column] = it })
        }

        items.clear()
        for (column in 0 until config.fieldWidth)
            items.add(arrayListOf())

    } }

    private var columnTouchdown: ((columnIndex: Int, columnXY: PointF) -> Boolean)?=null
    override fun withColumnTouchdownListener(handler: ((columnIndex: Int, columnXY: PointF) -> Boolean)?): ClassicGameField {
        this.columnTouchdown = handler
        return this
    }

    private fun tryGetHighlightObject(x: Float, y: Float): GameObject? { synchronized(itemsLo) {
        return highlightItems.values.firstOrNull { x in it.x..(it.x + it.width) && y in it.y..(it.y + it.height) }
    } }

    private var highlightedColumn: GameObject? = null
    override fun onTouch(me: MotionEvent, sp: SceneParams): Boolean {
        if (me.action == MotionEvent.ACTION_DOWN && highlightedColumn == null) {
            highlightedColumn = tryGetHighlightObject(me.x / sp.scale, me.y / sp.scale)
            if (highlightedColumn != null) {
                if ((columnTouchdown?.invoke(highlightedColumn!!.tag as Int, PointF(highlightedColumn!!.x, highlightedColumn!!.y)) == false))
                    highlightedColumn = null
                else {
                    highlightedColumn!!.alpha = 1f
                    highlightedColumn!!.requestDraw()
                    return true
                }
            }
        }
        if (me.action == MotionEvent.ACTION_UP && highlightedColumn != null) {
            highlightedColumn!!.alpha = 0f
            highlightedColumn!!.requestDraw()
            highlightedColumn = null
            return true
        }
        return false
    }

    override fun clear() { synchronized(itemsLo) {
        items.flatten().forEach { it.requestRemove() }
    }}

    override fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams) { synchronized(itemsLo) {
        items.forEach { it.clearRemoved() }
    } }

    override fun canBePlaced(block: Block, column: Int): Boolean { synchronized(itemsLo) {
        if (column in 0 until config.fieldHeight && items[column].size < config.fieldHeight)
            return true
        return false
    } }

    override fun placeTo(block: Block, columnIndex: Int) { synchronized(itemsLo) {
        val column = highlightItems[columnIndex]!!
        val columnItemsCount = items[columnIndex].size
        val dstXY = PointF(column.x, column.y + columnItemsCount * (config.blockHeightPx + config.blockGapVPx))

        if (columnItemsCount >= config.fieldHeight)
            return

        block.withLocation(column.x, config.fieldHeightPx + 1)
            .add(GameObjectAnimation(GameObjectAnimationTypeId.TRANSLATE)
                .withParam(GameObjectAnimation.PARAM_DEST_XY, dstXY)
                .withParam(GameObjectAnimation.PARAM_DELAY, 100L)
                .withParam(GameObjectAnimation.PARAM_SPEED, 0.004f)
                .withParam(GameObjectAnimation.PARAM_INTERPOLATOR, DecelerateInterpolator()))

        items[columnIndex].add(block)
    } }
}