package com.template.drugsreminder.models

import com.template.drugsreminder.utils.resetToStartOfDay
import java.util.*

data class MedicineModel(
    val name: String,
    val img: Int,
    var frequency: Frequency,
    val duration: DurationModel,
    val takingTimes: List<TakingTime>
)

data class DurationModel(
    val startDate: Date,
    val duration: Duration
)

data class ScheduleMedicineModel(
    val name: String,
    val picture: Int,
    val time: Date,
    val dosage: Double
)

data class TakingTime(
    var takingTime: Date = Calendar.getInstance().resetToStartOfDay().time,
    var dosage: Double = 1.0
)