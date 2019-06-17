package com.template.drugsreminder.main

import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.data.medicine.MedicineManager
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.dayOfWeek
import com.template.drugsreminder.utils.daysTo
import java.util.*
import kotlin.collections.ArrayList


class MainViewModel : BaseViewModel() {

    lateinit var medicineList: List<MedicineModel>

    lateinit var pictures: List<Int>

    fun deserializeData() {
        medicineList = MedicineManager.list
    }

    fun selectData(date: Date): ArrayList<ScheduleMedicineModel> {
        val scheduleMedicineList = ArrayList<ScheduleMedicineModel>()

        for (item in medicineList) {
            if (item.duration.startDate >= date) continue

            val needToAddItem = when (item.duration.duration) {
                is WithoutDate -> checkFrequency(item.frequency, date, item)
                is TillDate -> item.duration.duration.date >= date && checkFrequency(item.frequency, date, item)
                is DurationCount -> {
                    val endDate = Calendar.getInstance()
                        .apply { add(Calendar.DAY_OF_MONTH, item.duration.duration.durationCount) }.time
                    endDate >= date && checkFrequency(item.frequency, date, item)
                }
                else -> false
            }
            if (needToAddItem) scheduleMedicineList.add(ScheduleMedicineModel(item.name, pictures[item.img]))
        }
        return scheduleMedicineList
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