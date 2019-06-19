package com.template.drugsreminder.main

import androidx.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.data.medicine.MedicineManager
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.dayOfWeek
import com.template.drugsreminder.utils.daysTo
import com.template.drugsreminder.utils.resetToStartOfDay
import java.util.*
import kotlin.collections.ArrayList


class MainViewModel : BaseViewModel() {

    val data = MutableLiveData<List<ScheduleMedicineModel>>()

    lateinit var pictures: List<Int>

    init {
        data.value = ArrayList()
    }

    private var internalData: List<MedicineModel>? = null

    fun load() {
        internalData = MedicineManager.load()
    }

    fun selectDate(date: Date) {
        if(internalData == null) return
        val scheduleMedicineList = ArrayList<ScheduleMedicineModel>()

        for (item in internalData!!) {
            if (item.duration.startDate.resetToStartOfDay() > date) continue

            val needToAddItem = when (item.duration.duration) {
                is WithoutDate -> checkFrequency(item.frequency, date, item)
                is TillDate -> item.duration.duration.date.resetToStartOfDay() >= date && checkFrequency(item.frequency, date, item)
                is DurationCount -> {
                    val endDate = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, item.duration.duration.durationCount) }.time
                    endDate >= date && checkFrequency(item.frequency, date, item)
                }
                else -> false
            }
            if (needToAddItem)
                scheduleMedicineList.apply {
                    item.takingTimes.onEach {
                        add(
                            ScheduleMedicineModel(
                                item.name,
                                pictures[item.img],
                                it.takingTime,
                                it.dosage
                            )
                        )
                    }
                }
        }
        data.value = scheduleMedicineList
    }

    private fun checkFrequency(frequency: Frequency, date: Date, item: MedicineModel) =
        when (frequency) {
            is DaysAWeek -> {
                val daysSinceStart = item.duration.startDate.daysTo(date)
                daysSinceStart % frequency.daysCount == 0
            }
            is Weekly -> {
                frequency.weekDays.contains(date.dayOfWeek)
            }
            is Cycle -> {
                true
            }
            is TimesADay -> true
            is HoursADay -> true
            else -> false
        }


}