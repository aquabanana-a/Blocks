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
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject.Companion.removeTrash
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
    private val items = ArrayList<ArrayList<GameObject?>>()
    private val itemsLo = Any()

    override val objects: List<GameObject> get() = synchronized(itemsLo) { items.flatten().filterNotNull() + background }

    private fun buildMoveAnim(dstXY: PointF) = GameObjectAnimation(GameObjectAnimationTypeId.TRANSLATE)
        .withParam(GameObjectAnimation.PARAM_DEST_XY, dstXY)
        .withParam(GameObjectAnimation.PARAM_DELAY, 100L)
        .withParam(GameObjectAnimation.PARAM_SPEED, 0.004f)
        .withParam(GameObjectAnimation.PARAM_INTERPOLATOR, DecelerateInterpolator())

    override fun init() { synchronized(itemsLo) {
        background = GameObject().apply {
            width = config.fieldWidthPx
            height = config.fieldHeightPx
        }

        for (column in 0 until config.fieldWidthBl) {
            val columnX = column * (config.blockWidthPx + config.blockGapHPx)

            background.requestAnimation(GameObject().apply {
                x = columnX
                width = config.blockWidthPx
                height = config.fieldHeightPx
                assetId = R.drawable.bg_03
            })

            background.requestAnimation(GameObject().apply {
                x = columnX
                width = config.blockWidthPx
                height = config.fieldHeightPx
                color = 0x10FFFFFF
                alpha = 0f
                tag = column
            }.also { highlightItems[column] = it })
        }

        items.clear()
        for (column in 0 until config.fieldWidthBl)
            items.add(arrayOfNulls<GameObject?>(config.fieldHeightBl).toCollection(ArrayList()))

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
                if (!canBePlaced(null, highlightedColumn!!.tag as Int) || columnTouchdown?.invoke(highlightedColumn!!.tag as Int, PointF(highlightedColumn!!.x, highlightedColumn!!.y)) == false)
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
        items.flatten().forEach { it?.requestRemove() }
    }}

    override fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams) { synchronized(itemsLo) {
        items.forEach { it.removeTrash() }
    } }

    override fun canBePlaced(block: Block?, columnIndex: Int): Boolean { synchronized(itemsLo) {
        if (columnIndex in 0 until config.fieldHeightBl) {
            val r = findFirstAvailableRowInColumn(columnIndex)
            if (r != -1)
                return true
        }
        return false
    } }

    override fun placeTo(block: Block, columnIndex: Int) { synchronized(itemsLo) {
        val column = highlightItems[columnIndex]!!
        val rowIndex = findFirstAvailableRowInColumn(columnIndex)

        if (rowIndex < 0)
            return

        block.withLocation(column.x, config.fieldHeightPx + config.blockGapVPx + 1)
            .disableMerge()
            .requestAnimation(buildMoveAnim(calcItemLocation(columnIndex, rowIndex))
                .withOnComplete {
                    block.enableMerge()
                    val b = getItemById(it) as? Block
                    if (b != null)
                        merge(b)
                })

        items[columnIndex][rowIndex] = block
        block.withOnRemoved {
            removeItemById(it.id)



//            synchronized(itemsLo) {
//            val indexInColumn = (block.tag as Point).y
//            items[columnIndex].remove(block)
//
////            Log.d("mymy", "removed at: ${indexInColumn}")
////            if (items[columnIndex].size > indexInColumn) {
////                val itemsToShift = items[columnIndex].subList(indexInColumn, items[columnIndex].size)
////                for (i in itemsToShift.indices) {
////                    itemsToShift[i].requestAnimation(buildMoveAnim(calcItemLocation(columnIndex, indexInColumn + i)))
////                }
////            }
//        }

        }
    } }

    private fun findFirstAvailableRowInColumn(columnIndex: Int): Int { synchronized(itemsLo) {
        val c = items[columnIndex]
        var ret = -1
        for (r in c.indices.reversed())
            if (c[r] == null)
                ret = r
            else
                break
        return ret
    } }

    private fun calcItemLocation(column: Int, row: Int): PointF {
        val co = highlightItems[column]!!
        return PointF(co.x, co.y + row * (config.blockHeightPx + config.blockGapVPx))
    }

    private fun getItemById(id: Long): GameObject? { synchronized(itemsLo) {
        return items.flatten().firstOrNull { it?.id == id }
    } }

    private fun getItemByMesh(c: Int, r: Int): GameObject? { synchronized(itemsLo) {
        if (c in 0 until items.size && r in 0 until items[c].size)
            return items[c][r]
        return null
    } }

    private fun getItemMesh(id: Long): Pair<Int, Int>? { synchronized(itemsLo) {
        for (c in items.indices)
            for (r in items[c].indices)
                if (items[c][r]?.id == id)
                    return Pair(c, r)

        return null
    } }

    private fun removeItemById(id: Long) { synchronized(itemsLo) {
        for (c in items.indices)
            for (r in items[c].indices)
                if (items[c][r]?.id == id)
                    items[c][r] = null
    } }

    private fun getMergeCandidates(src: GameObject): List<GameObject> {
        val cr = getItemMesh(src.id)
        val ret = arrayListOf<GameObject>()

        if (cr == null)
            return ret

        var go = getItemByMesh(cr.first, cr.second - 1)
        if (go?.canMerge == true) ret.add(go)

        go = getItemByMesh(cr.first + 1, cr.second)
        if (go?.canMerge == true) ret.add(go)

        go = getItemByMesh(cr.first, cr.second + 1)
        if (go?.canMerge == true) ret.add(go)

        go = getItemByMesh(cr.first - 1, cr.second)
        if (go?.canMerge == true) ret.add(go)

        return ret
    }

    private fun merge(src: Block) { synchronized(itemsLo) {
        val candidates = getMergeCandidates(src).filter { it is Block && it.typeId == src.typeId }
        if (candidates.isEmpty())
            return

        val dst = candidates.first()
        src.disableMerge()
        dst.disableMerge()

        src.requestAnimation(buildMoveAnim(dst.location).withOnComplete {
            dst.requestRemove()
            src.requestRemove()
        })
        src.requestDraw()
    } }
}