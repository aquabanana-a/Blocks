package com.fromfinalform.blocks.common.ui.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<in F : Fragment, out T : ViewBinding>(
    val viewBinder: (F) -> T,
) : ViewBindingProperty<F, T>(viewBinder) {
    override fun getLifecycleOwner(thisRef: F): LifecycleOwner {
        try {
            return thisRef.viewLifecycleOwner
        } catch (ignored: IllegalStateException) {
            error("Should not attempt to get bindings when Fragment views are destroyed.")
        }
    }

}

class DialogFragmentViewBindingDelegate<in F : DialogFragment, out T : ViewBinding>(
    val viewBinder: (F) -> T,
) : ViewBindingProperty<F, T>(viewBinder) {
    override fun getLifecycleOwner(thisRef: F): LifecycleOwner {
        return if (thisRef.showsDialog) {
            thisRef
        } else {
            try {
                thisRef.viewLifecycleOwner
            } catch (ignored: IllegalStateException) {
                error("Should not attempt to get bindings when Fragment views are destroyed.")
            }
        }
    }
}

abstract class ViewBindingProperty<in R : Any, out T : ViewBinding>(
    val viewBindingFactory: (R) -> T,
) : ReadOnlyProperty<R, T> {

    private var binding: T? = null
    private val lifecycleObserver = BindingLifecycleObserver()

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = getLifecycleOwner(thisRef).lifecycle

        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        lifecycle.addObserver(lifecycleObserver)

        return viewBindingFactory as? T ?: viewBindingFactory(thisRef).also { viewBinding ->
            this.binding = viewBinding
        }
    }

    private inner class BindingLifecycleObserver : DefaultLifecycleObserver {

        private val mainHandler = Handler(Looper.getMainLooper())

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            mainHandler.post {
                binding = null
            }
        }
    }
}
