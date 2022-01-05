package com.fromfinalform.blocks.domain.model.game

import android.view.MotionEvent
import com.fromfinalform.blocks.domain.model.game.`object`.GameObject
import com.fromfinalform.blocks.domain.model.game.`object`.block.Block
import com.fromfinalform.blocks.presentation.model.graphics.renderer.RenderParams
import com.fromfinalform.blocks.presentation.model.graphics.renderer.SceneParams
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface IGameLooper : IGameObjectsHolder {

    val objectsDirtyFlat: List<GameObject>

    val currentBlock: BehaviorSubject<Block?>
    val trashBlock: BehaviorSubject<Block?>

    fun onTouch(me: MotionEvent, sp: SceneParams): Boolean

    fun init()
    fun start()
    fun stop()

    fun onFrameDrawn(renderParams: RenderParams, sceneParams: SceneParams)

}