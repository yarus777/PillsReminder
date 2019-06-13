package com.template.drugsreminder.models

import java.util.*

abstract class Duration {
    abstract val category: DurationCategory
}

class WithoutDate : Duration() {
    override val category = DurationCategory.WithoutDate
}

data class TillDate(var date: Date) : Duration() {
    override val category = DurationCategory.TillDate
}

data class DurationCount(var durationCount: Int) : Duration() {
    override val category = DurationCategory.DurationCount
}

enum class DurationCategory {
    WithoutDate,
    TillDate,
    DurationCount
}