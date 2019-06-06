package com.template.drugsreminder.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?)->Unit) {
    this.observe(owner, Observer<T> { t -> observer(t) })
}