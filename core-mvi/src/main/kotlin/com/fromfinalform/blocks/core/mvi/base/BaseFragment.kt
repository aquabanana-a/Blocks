package com.fromfinalform.blocks.core.mvi.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class BaseFragment<S: State, VM: BaseViewModel<S>>(
    @LayoutRes contentLayoutId: Int,
) : BaseView, Fragment(contentLayoutId) {

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = createViewModel()
        lifecycle.addObserver(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bind(viewLifecycleOwner.lifecycleScope, this)
    }

    fun dispatchEvent(event: Event) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.putEvent(event)
        }
    }

    // создание модели курильщика
    private fun createViewModel(): VM {
        // e.g. we are AuthFragment<AuthViewModel>, get my genericSuperclass which is BaseFragment<AuthViewModel>
        val parameterizedType = javaClass.genericSuperclass as? ParameterizedType

        @Suppress("UNCHECKED_CAST")
        val viewModelClass = parameterizedType?.actualTypeArguments?.getOrNull(0) as? Class<VM>?
        return viewModelClass?.run { provideViewModel(this@run) }
            ?: throw IllegalArgumentException("ViewModel class for $this not found.")
    }

    protected open fun provideViewModel(viewModelClass: Class<VM>): VM {
        return ViewModelProvider(this).get(viewModelClass)
    }

    // создание модели нормального человека
//    abstract val viewModelClass: KClass<VM>
//    private fun createViewModel(): VM {
//        return ViewModelProvider(this).get(viewModelClass.javaObjectType)
//    }
}