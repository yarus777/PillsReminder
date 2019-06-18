package com.template.drugsreminder.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel

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