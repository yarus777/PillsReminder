package com.template.drugsreminder.main

import android.os.Environment
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.template.drugsreminder.base.BaseViewModel
import java.io.File
import com.template.drugsreminder.models.*
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel : BaseViewModel() {

    companion object {
        val file = File(Environment.getExternalStorageDirectory().path + "/medicine.json")
    }


    fun deserializeData() {
        if (file.exists())
            readFile()
    }

    private fun readFile() {
        val json = file.readText()

        val gsonBuilder = GsonBuilder()

        val deserializer = JsonDeserializer<MedicineModel> { json, typeOfT, context ->

            val jsonObject = json.asJsonObject

            val frequencyObject = jsonObject.get("frequency").asJsonObject
            val durationObject = jsonObject.get("duration").asJsonObject

            var frequency: Frequency? = null
            var duration: Duration? = null

            when {
                frequencyObject.has("timesaday") -> frequency = TimesADay(frequencyObject.get("timesaday").asInt)
                frequencyObject.has("hoursaday") -> frequency = HoursADay(frequencyObject.get("hoursaday").asInt)
                frequencyObject.has("daysaweek") -> frequency = DaysAWeek(frequencyObject.get("daysaweek").asInt)
                frequencyObject.has("weekly") -> {
                    val weekDays = frequencyObject.get("weekly").asJsonArray
                    frequency = Weekly((0..weekDays.size()).map { i -> weekDays.get(i).asInt }.toHashSet())
                }
                frequencyObject.has("cycle") -> {
                    val cycle = frequencyObject.get("cycle").asJsonObject
                    frequency =
                        Cycle(cycle.get("activedays").asInt, cycle.get("breakdays").asInt, cycle.get("cycleday").asInt)
                }
            }

            when {
                durationObject.has("withoutdate") -> duration = WithoutDate()
                durationObject.has("tilldate") -> duration = TillDate(Date(durationObject.get("tilldate").asLong))
                durationObject.has("durationcount") -> duration = DurationCount(durationObject.get("durationcount").asInt)
            }

            val durationModel = DurationModel(Date(durationObject.get("startdate").asLong), duration!!)

            MedicineModel(jsonObject.get("name").asString, frequency!!, durationModel)
        }

        gsonBuilder.registerTypeAdapter(MedicineModel::class.java, deserializer)

        val customGson = gsonBuilder.create()
        val model = customGson.fromJson(json, MedicineModel::class.java)
    }
}