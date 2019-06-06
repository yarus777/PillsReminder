package com.template.drugsreminder.addmedicine

import android.arch.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.Duration
import com.template.drugsreminder.models.Frequency
import com.template.drugsreminder.models.TimesADay

class AddMedicineViewModel : BaseViewModel() {
    companion object {
        private val DEFAULT_FREQUENCY = TimesADay(1)
        private val DEFAULT_DURATION = Duration.WithoutDate
    }

    val frequency = MutableLiveData<Frequency>().apply { value = DEFAULT_FREQUENCY }
    val duration = MutableLiveData<Duration>().apply { value = DEFAULT_DURATION }

}