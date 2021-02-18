/*
 * Created by S.Dobranos on 16.02.21 21:18
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.domain.model.game.mode.classic

import android.util.Log
import android.view.MotionEvent
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import javax.inject.Inject

class ClassicGameField : IGameField {

    @Inject constructor()
    @Inject lateinit var config: IGameConfig

    private lateinit var background: GameObject
    private val highlightItems = hashMapOf<Int/*column*/, GameObject>()
    private val items = hashMapOf<Pair<Int/*column*/, Int/*row*/>, GameObject>()
    private val itemsLo = Any()

    override val objects: List<GameObject> get() = synchronized(itemsLo) { items.values.toList() + background }

    override fun init() { synchronized(itemsLo) {
        background = GameObject()

        for (column in 0..config.fieldWidth) {
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
                color = 0x20FFFFFF
                alpha = 0f
            }.also { highlightItems[column] = it })
        }
    } }

    override fun highlightColumn(index: Int, value: Boolean) {
        TODO("Not yet implemented")
    }

    private fun tryGetHighlightObject(x: Float, y: Float): GameObject? { synchronized(itemsLo) {
        return highlightItems.values.firstOrNull { x in it.x..(it.x + it.width) && y in it.y..(it.y + it.height) }
    } }

    private var highlightedColumn: GameObject? = null
    override fun onTouch(me: MotionEvent, sp: SceneParams): Boolean {
        if (me.action == MotionEvent.ACTION_DOWN && highlightedColumn == null) {
            highlightedColumn = tryGetHighlightObject(me.x / sp.scale, me.y / sp.scale)
            if (highlightedColumn != null) {
                highlightedColumn!!.alpha = 1f
                highlightedColumn!!.requestDraw()
                return true
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
}