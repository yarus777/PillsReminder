package com.template.drugsreminder.addmedicine

import android.arch.lifecycle.MutableLiveData
import com.google.gson.*
import com.template.drugsreminder.base.BaseViewModel
import com.template.drugsreminder.models.*
import java.util.*
import android.os.Environment
import java.io.File


class AddMedicineViewModel : BaseViewModel() {
    companion object {
        private val DEFAULT_FREQUENCY = TimesADay(1)
        private val DEFAULT_DURATION = WithoutDate()
        val file = File(Environment.getExternalStorageDirectory().path + "/medicine.json")
    }

    val medicineName = MutableLiveData<String>()

    val frequency = MutableLiveData<Frequency>().apply { value = DEFAULT_FREQUENCY }
    val duration = MutableLiveData<Duration>().apply { value = DEFAULT_DURATION }
    val startDate = MutableLiveData<Date>().apply { value = Calendar.getInstance().time }

    fun serializeData() {

        val model = MedicineModel(
            medicineName.value!!, frequency.value!!, DurationModel(
                startDate.value!!,
                duration.value!!
            )
        )

        val serializer = JsonSerializer<MedicineModel> { medicineModel, type, context ->
            val medicineObject = JsonObject()
            medicineObject.addProperty("name", medicineModel.name)

            val frequencyObject = JsonObject()

            when (val frequency = medicineModel.frequency) {
                is TimesADay -> {
                    frequencyObject.addProperty("timesaday", frequency.timesCount)
                }
                is HoursADay -> {
                    frequencyObject.addProperty("hoursaday", frequency.hoursCount)
                }
                is DaysAWeek -> {
                    frequencyObject.addProperty("daysaweek", frequency.daysCount)
                }
                is Weekly -> {
                    val weekDays = JsonArray()
                    for (item in frequency.weekDays) {
                        weekDays.add(item)
                    }
                    frequencyObject.add("weekly", weekDays)
                }
                is Cycle -> {
                    val cycleObject = JsonObject()
                    cycleObject.addProperty("activedays", frequency.activeDaysCount)
                    cycleObject.addProperty("breakdays", frequency.breakDaysCount)
                    cycleObject.addProperty("cycleday", frequency.cycleDay)
                    frequencyObject.add("cycle", cycleObject)
                }
            }

            medicineObject.add("frequency", frequencyObject)

            val durationObject = JsonObject()

            durationObject.addProperty("startdate", medicineModel.duration.startDate?.time)

            when (val duration = medicineModel.duration.duration) {

                is WithoutDate -> {
                    durationObject.addProperty("withoutdate", "")
                }
                is TillDate -> {
                    durationObject.addProperty("tilldate", duration.date.time)
                }
                is DurationCount -> {
                    durationObject.addProperty("durationcount", duration.durationCount)
                }

            }

            medicineObject.add("duration", durationObject)

            medicineObject
        }

        val builder = GsonBuilder().registerTypeAdapter(MedicineModel::class.java, serializer)
        val customGson = builder.create()
        val customJson = customGson.toJson(model)

        writeFile(customJson)
    }

    private fun writeFile(json: String) {
        file.writeText(json)
    }

}

