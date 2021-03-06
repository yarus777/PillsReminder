package com.template.drugsreminder.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?)->Unit) {
    this.observe(owner, Observer<T> { t -> observer(t) })
}

fun <T> MutableLiveData<T>.invalidate() {
    this.value = this.value
}