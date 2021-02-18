/*
 * Created by S.Dobranos on 16.02.21 21:12
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.dagger.module

import com.fromfinalform.blocks.domain.model.game.IGameField
import com.fromfinalform.blocks.domain.model.game.IGameLooper
import com.fromfinalform.blocks.domain.model.game.configuration.IGameConfig
import com.fromfinalform.blocks.domain.model.game.mode.classic.ClassicGameConfig
import com.fromfinalform.blocks.domain.model.game.mode.classic.ClassicGameField
import com.fromfinalform.blocks.domain.model.game.mode.classic.ClassicGameLooper
import dagger.Binds
import dagger.Module

@Module
interface ClassicGameModule {
    @Binds fun bindConfig(config: ClassicGameConfig): IGameConfig
    @Binds fun bindField(field: ClassicGameField): IGameField
    @Binds fun bindLooper(looper: ClassicGameLooper): IGameLooper
}