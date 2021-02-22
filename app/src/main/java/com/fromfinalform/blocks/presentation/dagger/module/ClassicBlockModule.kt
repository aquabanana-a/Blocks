/*
 * Created by S.Dobranos on 22.02.21 14:29
 * Copyright (c) 2021. All rights reserved.
 */

package com.fromfinalform.blocks.presentation.dagger.module

import com.fromfinalform.blocks.data.repository.ClassicBlockTypeRepository
import com.fromfinalform.blocks.domain.repository.IBlockTypeRepository
import dagger.Module
import dagger.Provides

@Module
class ClassicBlockModule {
    @Provides
    open fun provideTypeRepo(): IBlockTypeRepository {
        return ClassicBlockTypeRepository().apply { initialize() }
    }
}