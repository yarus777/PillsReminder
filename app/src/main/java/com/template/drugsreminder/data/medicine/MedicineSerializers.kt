package com.template.drugsreminder.data.medicine

import com.google.gson.*
import com.template.drugsreminder.models.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class MedicineSerializer : JsonSerializer<MedicineModel>, JsonDeserializer<MedicineModel> {
    override fun serialize(
        medicineModel: MedicineModel,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ) = JsonObject().apply {
        addProperty("name", medicineModel.name)
        addProperty("picture", medicineModel.img)

        add("frequency", JsonObject().apply {
            when (val frequency = medicineModel.frequency) {
                is TimesADay -> {
                    addProperty("timesaday", frequency.timesCount)
                }
                is HoursADay -> {
                    addProperty("hoursaday", frequency.hoursCount)
                }
                is DaysAWeek -> {
                    addProperty("daysaweek", frequency.daysCount)
                }
                is Weekly -> {
                    add("weekly", JsonArray().apply { frequency.weekDays.onEach { add(it.code) } })
                }
                is Cycle -> {
                    add("cycle", JsonObject().apply {
                        addProperty("activedays", frequency.activeDaysCount)
                        addProperty("breakdays", frequency.breakDaysCount)
                        addProperty("cycleday", frequency.cycleDay)
                    })
                }
            }
        })

        add("duration", JsonObject().apply {
            addProperty("startdate", medicineModel.duration.startDate.time)
            when (val duration = medicineModel.duration.duration) {
                is WithoutDate -> {
                    addProperty("withoutdate", "")
                }
                is TillDate -> {
                    addProperty("tilldate", duration.date.time)
                }
                is DurationCount -> {
                    addProperty("durationcount", duration.durationCount)
                }
            }
        })


        add("takingtimes", JsonArray().apply {
            medicineModel.takingTimes.onEach {
                add(JsonObject().apply {
                    addProperty("time", it.takingTime.time)
                    addProperty("dosage", it.dosage)
                })
            }
        })
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MedicineModel {

        lateinit var frequency: Frequency
        lateinit var duration: Duration
        val durationModel: DurationModel
        val takingTimes: ArrayList<TakingTime> = ArrayList()

        val jsonObject = json.asJsonObject.apply {

            get("frequency").asJsonObject.apply {
                when {
                    has("timesaday") -> frequency = TimesADay(get("timesaday").asInt)
                    has("hoursaday") -> frequency = HoursADay(get("hoursaday").asInt)
                    has("daysaweek") -> frequency = DaysAWeek(get("daysaweek").asInt)
                    has("weekly") -> {
                        frequency = Weekly(get("weekly").asJsonArray.map { WeekDay.fromCode(it.asInt) }.toHashSet())
                    }
                    has("cycle") -> {
                        get("cycle").asJsonObject.apply {
                            frequency =
                                Cycle(
                                    get("activedays").asInt,
                                    get("breakdays").asInt,
                                    get("cycleday").asInt
                                )
                        }

                    }
                }
            }

            get("duration").asJsonObject.apply {
                when {
                    has("withoutdate") -> duration = WithoutDate()
                    has("tilldate") -> duration = TillDate(Date(get("tilldate").asLong))
                    has("durationcount") -> duration = DurationCount(get("durationcount").asInt)
                }
                durationModel = DurationModel(Date(get("startdate").asLong), duration)
            }

            takingTimes.apply {
                get("takingtimes").asJsonArray.onEach {
                    add(
                        TakingTime(
                            Date(it.asJsonObject.get("time").asLong),
                            it.asJsonObject.get("dosage").asDouble
                        )
                    )
                }
            }
        }

        return MedicineModel(
            jsonObject.get("name").asString,
            jsonObject.get("picture").asInt,
            frequency,
            durationModel, takingTimes
        )
    }
}


class MedicineListSerializer : JsonSerializer<List<MedicineModel>>, JsonDeserializer<List<MedicineModel>> {
    override fun serialize(src: List<MedicineModel>, typeOfSrc: Type?, context: JsonSerializationContext) =
        JsonArray().apply {
            src.map { add(context.serialize(it)) }
        }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext) =
        json.asJsonArray.map { context.deserialize<MedicineModel>(it, MedicineModel::class.java) }

}
