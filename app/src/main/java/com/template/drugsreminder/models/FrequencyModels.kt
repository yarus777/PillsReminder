package com.template.drugsreminder.models

import java.util.*
import kotlin.collections.HashSet

abstract class Frequency {
    abstract val category: FrequencyCategory
}

data class TimesADay(var timesCount: Int) : Frequency() {
    override val category = FrequencyCategory.TimesADay
}

data class HoursADay(var hoursCount: Int) : Frequency() {
    override val category = FrequencyCategory.HoursADay
}

data class DaysAWeek(var daysCount: Int) : Frequency() {
    override val category = FrequencyCategory.DaysAWeek
}

data class Weekly(var weekDays: HashSet<WeekDay>) : Frequency() {
    override val category = FrequencyCategory.Weekly
}

data class Cycle(var activeDaysCount: Int, var breakDaysCount: Int, var cycleDay: Int) : Frequency() {
    override val category = FrequencyCategory.Cycle
}

enum class FrequencyCategory {
    TimesADay,
    HoursADay,
    DaysAWeek,
    Weekly,
    Cycle
}

enum class WeekDay(val code: Int, val calendarCode: Int) {
    Monday(0, Calendar.MONDAY),
    Tuesday(1, Calendar.TUESDAY),
    Wednesday(2, Calendar.WEDNESDAY),
    Thursday(3, Calendar.THURSDAY),
    Friday(4, Calendar.FRIDAY),
    Saturday(5, Calendar.SATURDAY),
    Sunday(6, Calendar.SUNDAY);

    companion object {
        fun fromCode(code: Int) = values().first { it.code == code }
        val workdays = hashSetOf(Monday, Tuesday, Wednesday, Thursday, Friday)
    }
}