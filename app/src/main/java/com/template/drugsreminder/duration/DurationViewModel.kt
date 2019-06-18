package com.template.drugsreminder.duration

import androidx.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.Duration
import com.template.drugsreminder.models.WithoutDate
import java.util.*

class DurationViewModel(
    private val currentDuration: MutableLiveData<Duration>,
    private val currentStartDate: MutableLiveData<Date>
) : BaseViewModel() {

    val duration = MutableLiveData<Duration>()

    val startDate = MutableLiveData<Date>()

    init {
        duration.value = currentDuration.value ?: WithoutDate()
        startDate.value = currentStartDate.value
    }

    fun saveDuration() {
        currentDuration.value = duration.value
        currentStartDate.value = startDate.value
    }

}