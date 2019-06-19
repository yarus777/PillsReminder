package com.template.drugsreminder.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import com.google.gson.reflect.TypeToken
import com.template.drugsreminder.models.WeekDay
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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

fun View.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false) =
    context.inflate(rId, parent, attachToParent)

fun Context.inflate(@LayoutRes rId: Int, parent: ViewGroup? = null, attachToParent: Boolean = false): View =
    LayoutInflater.from(this).inflate(rId, parent, attachToParent)

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

fun Date.resetToStartOfDay() =
    Calendar.getInstance().apply {
        time = this@resetToStartOfDay
        resetToStartOfDay()
    }.time!!


val Date.dayOfWeek: WeekDay
    get() {
        return Calendar.getInstance().apply { time = this@dayOfWeek }.get(Calendar.DAY_OF_WEEK)
            .let { calendarCode -> WeekDay.values().first { it.calendarCode == calendarCode } }
    }

fun <T> ArrayList<T>.fill(count: Int, itemProvider: () -> T) = apply {
    (0 until count).forEach { add(itemProvider()) }
}
