package com.template.drugsreminder.data.medicine

import com.google.gson.*
import com.template.drugsreminder.models.*
import java.lang.reflect.Type
import java.util.*

class MedicineSerializer : JsonSerializer<MedicineModel>, JsonDeserializer<MedicineModel> {
    override fun serialize(
        medicineModel: MedicineModel,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val medicineObject = JsonObject()
        medicineObject.addProperty("name", medicineModel.name)
        medicineObject.addProperty("picture", medicineModel.img)

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
                    weekDays.add(item.code)
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

        return medicineObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MedicineModel {
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
                val weekDays = frequencyObject.get("weekly").asJsonArray.map { WeekDay.fromCode(it.asInt) }.toHashSet()
                frequency = Weekly(weekDays)
            }
            frequencyObject.has("cycle") -> {
                val cycle = frequencyObject.get("cycle").asJsonObject
                frequency =
                    Cycle(
                        cycle.get("activedays").asInt,
                        cycle.get("breakdays").asInt,
                        cycle.get("cycleday").asInt
                    )
            }
        }

        when {
            durationObject.has("withoutdate") -> duration = WithoutDate()
            durationObject.has("tilldate") -> duration = TillDate(Date(durationObject.get("tilldate").asLong))
            durationObject.has("durationcount") -> duration =
                DurationCount(durationObject.get("durationcount").asInt)
        }

        val durationModel = DurationModel(Date(durationObject.get("startdate").asLong), duration!!)

        return MedicineModel(
            jsonObject.get("name").asString,
            jsonObject.get("picture").asInt,
            frequency!!,
            durationModel
        )
    }
}


class MedicineListSerializer : JsonSerializer<List<MedicineModel>>, JsonDeserializer<List<MedicineModel>> {
    override fun serialize(src: List<MedicineModel>, typeOfSrc: Type?, context: JsonSerializationContext) =
        JsonArray().apply {
            src.map { context.serialize(it).let { jsonElem -> add(jsonElem) } }
        }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext) =
        json.asJsonArray.map { context.deserialize<MedicineModel>(it, MedicineModel::class.java) }

}
