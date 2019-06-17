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

    private lateinit var medicines: MutableList<MedicineModel>

    fun init(context: Context) {
        file = File(context.applicationContext.cacheDir, FILENAME)
        medicines = load() ?: ArrayList()
    }

    private fun load(): MutableList<MedicineModel>? =
        if (file.exists()) gson.fromJson<List<MedicineModel>>(
            file.readText(),
            genericType<List<MedicineModel>>()
        ).toMutableList()
        else null

    private fun save(data: List<MedicineModel>) = file.writeText(gson.toJson(data))

    fun add(model: MedicineModel) {
        medicines.apply {
            add(model)
            save(medicines)
        }
    }

    val list: List<MedicineModel>
        get() = medicines
}