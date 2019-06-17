package com.template.drugsreminder.base

import android.app.Application
import com.template.drugsreminder.data.medicine.MedicineManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MedicineManager.init(this)
    }
}