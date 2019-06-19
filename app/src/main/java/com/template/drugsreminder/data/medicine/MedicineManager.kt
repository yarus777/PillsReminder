package com.template.drugsreminder.data.medicine

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.template.drugsreminder.models.MedicineModel
import com.template.drugsreminder.utils.genericType
import java.io.File

object MedicineManager {
    private const val FILENAME = "medicine.json"
    private lateinit var file: File

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(MedicineModel::class.java, MedicineSerializer())
        .registerTypeAdapter(genericType<List<MedicineModel>>(), MedicineListSerializer())
        .create()

    private var medicines: MutableList<MedicineModel>? = null

    fun init(context: Context) {
        file = File(context.applicationContext.cacheDir, FILENAME)
    }

    fun load(): MutableList<MedicineModel> {
        if(medicines != null) return medicines!!
        medicines = if (file.exists()) {
                  gson.fromJson<List<MedicineModel>>(
                file.readText(),
                genericType<List<MedicineModel>>()
            ).toMutableList()
        } else null
        if(medicines == null) medicines = ArrayList()
        return medicines!!
    }

    private fun save(data: List<MedicineModel>) = file.writeText(gson.toJson(data))

    fun add(model: MedicineModel) {
        if(medicines == null) medicines = ArrayList()
        medicines!!.apply {
            add(model)
            save(this)
        }
    }
}