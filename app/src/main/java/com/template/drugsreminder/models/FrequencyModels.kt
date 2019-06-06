package com.template.drugsreminder.models

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

data class Weekly(var weekDays: List<Int>) : Frequency() {
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