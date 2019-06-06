package com.template.drugsreminder.frequency

import android.arch.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.Frequency
import com.template.drugsreminder.models.TimesADay

class FrequencyViewModel(private val currentFrequency: MutableLiveData<Frequency>) : BaseViewModel() {
    val frequency = MutableLiveData<Frequency>()

    init {
        frequency.value = currentFrequency.value ?: TimesADay(1)
    }

    fun saveFrequency() {
        currentFrequency.value = frequency.value
    }
}