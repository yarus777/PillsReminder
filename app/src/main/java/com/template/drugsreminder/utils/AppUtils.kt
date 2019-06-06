package com.template.drugsreminder.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

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