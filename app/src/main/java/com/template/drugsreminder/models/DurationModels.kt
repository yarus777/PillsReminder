package com.template.drugsreminder.models

sealed class Duration {
    object WithoutDate : Duration()
    object TillDate : Duration()
    object DurationCount : Duration()
}