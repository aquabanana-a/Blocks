/*
 * Created by S.Dobranos on 16.02.21 21:11
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.dagger

import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.presentation.dagger.module.ClassicBlockModule
import com.fromfinalform.blocks.presentation.dagger.module.ClassicGameModule
import com.fromfinalform.blocks.presentation.presenter.GamePresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ClassicGameModule::class, ClassicBlockModule::class, ])
interface GameComponent {
    fun getConfig(): IGameConfig
    fun getField(): IGameField
    fun getLooper(): IGameLooper

    fun injectGamePresenter(presenter: GamePresenter)
}