package com.fromfinalform.blocks.core.mvi.common

import com.fromfinalform.blocks.core.mvi.base.BaseFragment
import com.fromfinalform.blocks.core.mvi.base.Effect
import com.fromfinalform.blocks.core.mvi.base.State
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import java.io.Serializable

abstract class CommonFragment<VM: CommonViewModel>(
    @LayoutRes contentLayoutId: Int
) : BaseFragment<CommonState, VM>(contentLayoutId) {

    val navigationData: Serializable?
        get() = arguments?.getSerializable(COMMON_NAVIGATION_DATA_KEY)

    override suspend fun renderState(state: State) {}

    @CallSuper
    override suspend fun handleEffect(effect: Effect) {
        when (effect) {
            is CommonEffect.NavigateTo -> with(effect) {
                val navController = hostId?.run { Navigation.findNavController(requireActivity(), this) } ?: findNavController()
                navController.navigate(screenId, Bundle().apply { putSerializable(COMMON_NAVIGATION_DATA_KEY, data) })
            }
            is CommonEffect.NavigateBack -> {
                findNavController().popBackStack()
            }
        }
    }
}