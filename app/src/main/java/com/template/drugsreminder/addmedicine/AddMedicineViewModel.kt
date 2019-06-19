package com.template.drugsreminder.addmedicine

import androidx.lifecycle.MutableLiveData
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.*
import java.util.*
import com.template.drugsreminder.data.medicine.MedicineManager
import com.template.drugsreminder.utils.fill
import com.template.drugsreminder.utils.invalidate
import kotlin.collections.ArrayList


class AddMedicineViewModel : BaseViewModel() {
    companion object {
        private val DEFAULT_FREQUENCY = TimesADay(1)
        private val DEFAULT_DURATION = WithoutDate()
    }

    val medicineName = MutableLiveData<String>()

    val frequency = MutableLiveData<Frequency>().apply { value = DEFAULT_FREQUENCY }
    val duration = MutableLiveData<Duration>().apply { value = DEFAULT_DURATION }
    val startDate = MutableLiveData<Date>().apply { value = Calendar.getInstance().time }

    val takingTimes = MutableLiveData<TakingTimes>()
    val selectedIcon = MutableLiveData<MedicineIcon>()
    val icons = (0 until 14).map { MedicineIcon(it) }

    init {
        selectIcon(icons[0])
    }

    fun save() { //todo наверное сюда передать как-то значения из списка TakingTimes (либо заполнять при изменении полей формы)
        val model = MedicineModel(medicineName.value!!, selectedIcon.value!!.id, frequency.value!!, DurationModel(
                startDate.value!!,
                duration.value!!), takingTimes.value!!.list)

        MedicineManager.add(model)
    }

    fun addEmptyTimeToSchedule() {
        takingTimes.value?.list?.add(TakingTime())
        takingTimes.invalidate()
    }

    fun removeTimeFromSchedule(item: TakingTime) {
        takingTimes.value?.list?.remove(item)
        takingTimes.invalidate()
    }

    fun selectIcon(icon: MedicineIcon){
        selectedIcon.value = icon
    }

    data class MedicineIcon(val id: Int)
    data class TakingTimes(val list: MutableList<TakingTime>, val editable: Boolean) {
        companion object {
            fun fixed(size: Int): TakingTimes {
                val data = ArrayList<TakingTime>().fill(size) { TakingTime() }
                return TakingTimes(data, false)
            }
            fun fixed(data: List<TakingTime>) = TakingTimes(data.toMutableList(), false)
            fun float(initialSize: Int = 0): TakingTimes {
                val data = ArrayList<TakingTime>().fill(initialSize) { TakingTime() }
                return TakingTimes(data, true)
            }
        }
    }
}

