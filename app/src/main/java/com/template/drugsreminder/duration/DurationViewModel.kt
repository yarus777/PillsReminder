package com.template.drugsreminder.duration

import android.arch.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.Duration

class DurationViewModel(private val currentDuration: MutableLiveData<Duration>) : BaseViewModel() {

    val duration = MutableLiveData<Duration>()

    init {
        duration.value = currentDuration.value ?: Duration.WithoutDate
    }

    fun saveDuration() {
        currentDuration.value = duration.value
    }
}