package com.fromfinalform.blocks.domain.model.game.mode.classic

import android.graphics.PointF
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
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

    companion object {
        const val TIMELINE_MOVE = 1L
        const val TIMELINE_MERGE = 2L
    }

    @Inject constructor()
    @Inject lateinit var config: IGameConfig

    private lateinit var background: GameObject
    private val highlightItems = hashMapOf<Int/*column*/, GameObject>()
    private val items = ArrayList<ArrayList<GameObject?>>()
    private val itemsLo = Any()
    private val mergeLo = Any()
    private val itemsToRemove = arrayListOf<GameObject>()

    private var waitedObjectsCount = 0
    private val placementEnabled get() = waitedObjectsCount <= 0

    override val objects: List<GameObject> get() = /*synchronized(itemsLo) {*/ items.flatten().filterNotNull() + background /*}*/

    private fun buildMoveAnim(dstXY: PointF, timeline: Long, interpolator: Interpolator = DecelerateInterpolator()) = GameObjectAnimation(GameObjectAnimationTypeId.TRANSLATE)
        .withParam(GameObjectAnimation.PARAM_TIMELINE_ID, timeline)
        .withParam(GameObjectAnimation.PARAM_DEST_XY, dstXY)
        .withParam(GameObjectAnimation.PARAM_DELAY, 0L)
        .withParam(GameObjectAnimation.PARAM_SPEED, 0.004f)
        .withParam(GameObjectAnimation.PARAM_INTERPOLATOR, interpolator)

    override fun init() { /*synchronized(itemsLo) {*/
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

    } /*}*/

    private var columnTouchdown: ((columnIndex: Int, columnXY: PointF) -> Boolean)?=null
    override fun withColumnTouchdownListener(handler: ((columnIndex: Int, columnXY: PointF) -> Boolean)?): ClassicGameField {
        this.columnTouchdown = handler
        return this
    }

    private fun tryGetHighlightObject(x: Float, y: Float): GameObject? { /*synchronized(itemsLo) {*/
        return highlightItems.values.firstOrNull { x in it.x..(it.x + it.width) && y in it.y..(it.y + it.height) }
    } /*}*/

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

    override fun clear() { /*synchronized(itemsLo) {*/
        items.flatten().forEach { it?.requestRemove() }
    }/*}*/

    override fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams) { /*synchronized(itemsLo) {*/
        items.forEach { it.removeTrash() }
    } /*}*/

    override fun canBePlaced(block: Block?, columnIndex: Int): Boolean { synchronized(itemsLo) {
        if (placementEnabled && columnIndex in 0 until config.fieldHeightBl && items[columnIndex].last() == null)
            return true
        return false
    } }

    override fun placeTo(block: Block, columnIndex: Int) { /*synchronized(itemsLo) {*/
        val column = highlightItems[columnIndex]!!
        val rowIndex = findFirstAvailableRowInColumn(columnIndex)

        if (rowIndex < 0)
            return

        itemsToRemove.clear()
        waitedObjectsCount = 1
        block.withLocation(column.x, config.fieldHeightPx + config.blockGapVPx + 1)
            .disableMerge()
            .requestAnimation(buildMoveAnim(getItemLocation(columnIndex, rowIndex), TIMELINE_MOVE))
            .withOnAnimationQueueComplete { src, e -> /*synchronized(mergeLo) {*/
                if (e.src.id == TIMELINE_MOVE) {
                    src.enableMerge()
                    waitedObjectsCount += merge(src as Block)
                    waitedObjectsCount -= 1
                }
                return@withOnAnimationQueueComplete false
            } /*}*/

        setItemCoordinate(Pair(columnIndex, rowIndex), block)
        block.withOnRemoved { /*synchronized(mergeLo) {*/
            val coord = getItemCoordinate(it.id)
            val shiftObjects = removeItemById(it.id)

            waitedObjectsCount += shiftObjects.count { so -> itemsToRemove.firstOrNull { it.id == so.id } == null }
            waitedObjectsCount -= 1

            if (coord == null)
                return@withOnRemoved

            shiftObjects.forEachIndexed { i, it ->
                setItemCoordinate(getItemCoordinate(it.id), null)

                val newCoord = Pair(coord.first, coord.second + i)
                setItemCoordinate(newCoord, it as Block)

                //it.disableMerge()
                    it.requestAnimation(buildMoveAnim(getItemLocation(newCoord), TIMELINE_MOVE))
            }
        } /*}*/
    } /*}*/

    private fun findFirstAvailableRowInColumn(columnIndex: Int): Int { /*synchronized(itemsLo) {*/
        val c = items[columnIndex]
        var ret = -1
        for (r in c.indices.reversed())
            if (c[r] == null)
                ret = r
            else
                break
        return ret
    } /*}*/

    private fun getItemLocation(coord: Pair<Int, Int>) = getItemLocation(coord.first, coord.second)
    private fun getItemLocation(column: Int, row: Int): PointF {
        val co = highlightItems[column]!!
        return PointF(co.x, co.y + row * (config.blockHeightPx + config.blockGapVPx))
    }

    private fun getItemById(id: Long): GameObject? { /*synchronized(itemsLo) {*/
        return items.flatten().firstOrNull { it?.id == id }
    } /*}*/

    private fun setItemCoordinate(coord: Pair<Int, Int>?, item: Block?) {
        if (coord == null)
            return
        items[coord.first][coord.second] = item
    }

    private fun getItemByCoordinate(coord: Pair<Int, Int>): Block? { /*synchronized(itemsLo) {*/
        if (coord.first in 0 until config.fieldWidthBl && coord.second in 0 until config.fieldHeightBl)
            return items[coord.first][coord.second] as? Block
        return null
    } /*}*/

    private fun getItemCoordinate(id: Long): Pair<Int, Int>? { /*synchronized(itemsLo) {*/
        for (c in items.indices)
            for (r in items[c].indices)
                if (items[c][r]?.id == id)
                    return Pair(c, r)

        return null
    } /*}*/

    private fun removeItemById(id: Long): List<GameObject> { /*synchronized(itemsLo) {*/
        val ret = arrayListOf<GameObject>()
        var found = false
        for (c in items.indices) {
            for (r in items[c].indices) {
                if (items[c][r]?.id == id) {
                    items[c][r] = null
                    found = true
                }
                else if (found && items[c][r] != null) {
                    ret.add(items[c][r]!!)
                }
            }
            if (found)
                break
        }
        return ret
    } /*}*/

    private fun getMergeCandidates(src: Block): List<Block> {
        val cr = getItemCoordinate(src.id) ?: return arrayListOf()
        return arrayListOf(
                Pair(cr.first, cr.second - 1),
                Pair(cr.first + 1, cr.second),
                Pair(cr.first, cr.second + 1),
                Pair(cr.first - 1, cr.second))
            .mapNotNull { getItemByCoordinate(it) }
            .filter { it.canMerge && it.typeId == src.typeId && it.id != src.id }
    }

    private fun merge(src: Block): Int { /*synchronized(itemsLo) {*/
        val candidates = getMergeCandidates(src)
        //if (candidates.isEmpty())
            return 0

        src.disableMerge()
        itemsToRemove.add(src)

        candidates.forEach {
            it.disableMerge()
            itemsToRemove.add(it)

            it.withOnAnimationQueueComplete(null)
            it.requestAnimation(buildMoveAnim(src.location, TIMELINE_MOVE, AccelerateInterpolator()).withOnComplete {
                src.requestRemove()
            })
        }

        return candidates.size
    } /*}*/
}