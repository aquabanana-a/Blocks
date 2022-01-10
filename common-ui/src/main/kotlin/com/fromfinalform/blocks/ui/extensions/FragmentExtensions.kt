package com.fromfinalform.blocks.common.ui.extensions

import com.fromfinalform.blocks.common.ui.utils.DialogFragmentViewBindingDelegate
import com.fromfinalform.blocks.common.ui.utils.FragmentViewBindingDelegate
import com.fromfinalform.blocks.common.ui.utils.ViewBindingProperty
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.viewbinding.ViewBinding

fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}

fun Fragment.setChildFragmentResult(
    requestKey: String,
    result: Bundle,
) = childFragmentManager.setFragmentResult(requestKey, result)

fun Fragment.setChildFragmentResultListener(
    requestKey: String,
    listener: ((resultKey: String, bundle: Bundle) -> Unit),
) {
    childFragmentManager.setFragmentResultListener(requestKey, this, FragmentResultListener(listener))
}

fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (F) -> T,
): ViewBindingProperty<F, T> {
    return when (this) {
        is DialogFragment -> DialogFragmentViewBindingDelegate(viewBindingFactory) as ViewBindingProperty<F, T>
        else -> FragmentViewBindingDelegate(viewBindingFactory)
    }
}

inline fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
    crossinline viewBindingFactory: (View) -> T,
    crossinline viewProvider: (F) -> View = Fragment::requireView,
): ViewBindingProperty<F, T> {
    return viewBinding { fragment: F -> viewBindingFactory(viewProvider(fragment)) }
}


