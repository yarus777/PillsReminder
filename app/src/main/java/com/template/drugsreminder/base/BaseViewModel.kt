package com.template.drugsreminder.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModel

abstract class BaseViewModel protected constructor() : ViewModel(), LifecycleOwner {

    private val lifecycle = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    init {
        lifecycle.markState(Lifecycle.State.RESUMED)
    }

    override fun onCleared() {
        super.onCleared()
        lifecycle.markState(Lifecycle.State.DESTROYED)
    }
}