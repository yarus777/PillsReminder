package com.template.drugsreminder.addmedicine

import android.arch.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.*
import java.util.*
import com.template.drugsreminder.data.medicine.MedicineManager


class AddMedicineViewModel : BaseViewModel() {
    companion object {
        private val DEFAULT_FREQUENCY = TimesADay(1)
        private val DEFAULT_DURATION = WithoutDate()
    }

    val medicineName = MutableLiveData<String>()

    val medicinePicture = MutableLiveData<Int>()

    val frequency = MutableLiveData<Frequency>().apply { value = DEFAULT_FREQUENCY }
    val duration = MutableLiveData<Duration>().apply { value = DEFAULT_DURATION }
    val startDate = MutableLiveData<Date>().apply { value = Calendar.getInstance().time }

    fun serializeData() {

        val model = MedicineModel(
            medicineName.value!!, medicinePicture.value!!, frequency.value!!, DurationModel(
                startDate.value!!,
                duration.value!!
            )
        )

        MedicineManager.add(model)
    }


}

