package com.template.drugsreminder.models

import java.util.*

data class MedicineModel(
    val name: String,
    val img: Int,
    var frequency: Frequency,
    val duration: DurationModel
)

data class DurationModel(
    val startDate: Date,
    val duration: Duration
)

data class ScheduleMedicineModel(
    val name: String,
    val picture: Int
)