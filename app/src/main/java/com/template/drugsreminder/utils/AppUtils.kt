package com.template.drugsreminder.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.gson.reflect.TypeToken
import com.template.drugsreminder.models.WeekDay
import java.util.*
import java.util.concurrent.TimeUnit

fun EditText.addOnTextChangedListener(listener: (String) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener(s?.toString() ?: "")
        }
    }
    this.addTextChangedListener(watcher)
    return watcher
}

inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

fun Date.daysTo(date: Date): Int {
    val startTime = Calendar.getInstance().apply {
        time = this@daysTo
        resetToStartOfDay()
    }.timeInMillis
    val endTime = Calendar.getInstance().apply {
        time = date
        resetToStartOfDay()
    }.timeInMillis
    return TimeUnit.MILLISECONDS.toDays(endTime - startTime).toInt()
}

fun Calendar.resetToStartOfDay() = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

val Date.dayOfWeek: WeekDay
    get() {
        return Calendar.getInstance().apply { time = this@dayOfWeek }.get(Calendar.DAY_OF_WEEK).let { calendarCode -> WeekDay.values().first { it.calendarCode == calendarCode } }
    }