package com.fromfinalform.blocks.core.di

import android.app.Activity
import androidx.fragment.app.Fragment

interface Dependencies

interface HasComponentDependencies {
    val dependencies: DependenciesProvider
}
typealias DependenciesProvider = Map<Class<out Dependencies>, @JvmSuppressWildcards Dependencies>

fun Fragment.findComponentDependenciesProvider(): DependenciesProvider {
    var current: Fragment? = parentFragment
    while (current !is HasComponentDependencies?) {
        current = current?.parentFragment
    }

    val hasDaggerProviders = current ?: when (activity?.application) {
        is HasComponentDependencies -> activity?.application as HasComponentDependencies
        else -> throw IllegalStateException("Can not find suitable dagger provider for $this")
    }
    return hasDaggerProviders.dependencies
}

fun Activity.findComponentDependenciesProvider(): DependenciesProvider {
    val hasDaggerProviders = when (application) {
        is HasComponentDependencies -> application as HasComponentDependencies
        else -> throw IllegalStateException("Can not find suitable dagger provider for $this")
    }
    return hasDaggerProviders.dependencies
}

inline fun <reified T : Dependencies> Fragment.findComponentDependencies(): T {
    return findComponentDependenciesProvider()[T::class.java] as T
}

inline fun <reified T : Dependencies> Activity.findComponentDependencies(): T {
    return findComponentDependenciesProvider()[T::class.java] as T
}