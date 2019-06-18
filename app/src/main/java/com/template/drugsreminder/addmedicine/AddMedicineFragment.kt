package com.template.drugsreminder.addmedicine

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.duration.DurationViewModel
import com.template.drugsreminder.frequency.FrequencyViewModel
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.adapters.HeaderFooterRecyclerViewAdapter
import com.template.drugsreminder.utils.adapters.SimpleRecyclerViewAdapter
import com.template.drugsreminder.utils.adapters.SimpleViewHolder
import com.template.drugsreminder.utils.addOnTextChangedListener
import com.template.drugsreminder.utils.observe
import com.template.drugsreminder.utils.resetToStartOfDay
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.medicine_picture_item_view.*
import kotlinx.android.synthetic.main.time_item_view.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class AddMedicineFragment : BaseFragment() {
    private lateinit var model: AddMedicineViewModel

    private lateinit var adapter: SimpleRecyclerViewAdapter<MedicinePicture>

    private val fixedLengthAdapterProvider = { SimpleRecyclerViewAdapter(ArrayList(), { hashCode().toLong() }, ::TakingTimeViewHolder) }
    private val variableLengthAdapterProvider = { HeaderFooterRecyclerViewAdapter(ArrayList(), { hashCode().toLong() }, null, ::TakingTimeFooterHolder, ::TakingTimeViewHolder) }

    private val frequenciesData = arrayListOf(
            FrequencyData(
                    FrequencyCategory.TimesADay,
                    { resources.getString(R.string.x_times_a_day_pattern, (it as TimesADay).timesCount) },
                    AdapterWrapper(fixedLengthAdapterProvider(), { data }, { data = it })),
            FrequencyData(
                    FrequencyCategory.HoursADay,
                    { resources.getString(R.string.every_x_hours_a_day_pattern, (it as HoursADay).hoursCount) },
                    AdapterWrapper(fixedLengthAdapterProvider(), { data }, { data = it })),
            FrequencyData(
                    FrequencyCategory.DaysAWeek,
                    { resources.getString(R.string.every_x_days_pattern, (it as DaysAWeek).daysCount) },
                    AdapterWrapper(variableLengthAdapterProvider(), { data }, { data = it })),
            FrequencyData(
                    FrequencyCategory.Weekly,
                    {
                        val strings = resources.getStringArray(R.array.week_days).toList()
                        (it as Weekly).weekDays.sortedBy { it }.joinToString(", ") { day -> strings[day.code] }
                    },
                    AdapterWrapper(variableLengthAdapterProvider(), { data }, { data = it })),
            FrequencyData(
                    FrequencyCategory.Cycle,
                    { (it as Cycle).let { resources.getString(R.string.cycle_pattern, it.activeDaysCount, it.breakDaysCount) } },
                    AdapterWrapper(variableLengthAdapterProvider(), { data }, { data = it })))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config.apply {
            isBottomBarVisible = false
            apply()
        }

        model = getViewModel(AddMedicineViewModel::class)!!

        addMedicineDurationLayout.setOnClickListener {
            getNavController().navigate(R.id.action_addMedicine_to_duration, DurationViewModel(model.duration, model.startDate))
        }
        addMedicineFrequencyLayout.setOnClickListener {
            getNavController().navigate(R.id.action_addMedicine_to_frequency, FrequencyViewModel(model.frequency))
        }

        addMedicinePictureList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = SimpleRecyclerViewAdapter(preparePicturesAdapterData(), { imageResource.toLong() }, ::MedicinePictureViewHolder)
        addMedicinePictureList.adapter = adapter

        addMedicineTakingTimeList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        addMedicineSaveBtn.setOnClickListener {
            model.serializeData()
            getNavController().navigateUp()
        }

        addMedicineName.addOnTextChangedListener {
            model.medicineName.value = it
        }

        model.medicineName.observe(this) {
            addMedicineSaveBtn.isEnabled = it!!.isNotEmpty()
        }

        model.frequency.observe(this) {
            val data = if (it != null) frequenciesData.first { data -> data.category == it.category } else null
            addMedicineFrequencyValue.text = data?.labelProvider?.invoke(it!!) ?: ""
            addMedicineTakingTimeList.adapter = data?.adapter
            data?.data = prepareTakingTimeAdapterData(it)
        }
        model.duration.observe(this) {
            addMedicineDurationValue.text = when (it) {
                is WithoutDate -> resources.getString(R.string.no_end_date)
                is TillDate -> resources.getString(
                        R.string.till_date_pattern,
                        DateFormat.getDateInstance(DateFormat.SHORT).format(it.date)
                                                  )
                is DurationCount -> resources.getString(R.string.within_x_days_pattern, it.durationCount)
                else -> ""
            }
        }

    }

    private fun preparePicturesAdapterData(): List<MedicinePicture> {
        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        val data = (0..pictures.length()).map { i -> MedicinePicture(pictures.getResourceId(i, 0), false) }
        pictures.recycle()
        return data
    }

    private fun prepareTakingTimeAdapterData(frequency: Frequency?) = when (frequency) {
        is TimesADay -> {
            val interval = (24f * 60 / frequency.timesCount).roundToInt()
            val calendar = Calendar.getInstance().apply {
                resetToStartOfDay()
            }
            (0 until frequency.timesCount).map { i ->
                TakingTime((calendar.clone() as Calendar).apply {
                    add(Calendar.MINUTE, i * interval)
                }.time, 1.0)
            }
        }
        is HoursADay -> calculateEveryXHoursIntervals(frequency.hoursCount, Calendar.getInstance().apply { resetToStartOfDay() }.time)
        else -> ArrayList()
    }

    private fun calculateEveryXHoursIntervals(hoursCount: Int, startHour: Date): ArrayList<TakingTime> {
        var list: ArrayList<TakingTime> = ArrayList<TakingTime>()
        val calendar = Calendar.getInstance().apply {
            time = startHour
        }
        val nextDay = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            resetToStartOfDay()
        }
        val interval = hoursCount * 60
        while (calendar.apply { add(Calendar.HOUR_OF_DAY, hoursCount) }.time < nextDay.time) {
            list.add(TakingTime(calendar.time, 1.0))
        }
        return list
    }


    private fun onPictureClick(item: MedicinePicture) {
        adapter.data.forEach { it.isSelected = item == it }
        adapter.notifyDataSetChanged()
        model.medicinePicture.value = adapter.data.indexOf(item)
    }

    private inner class MedicinePictureViewHolder(parent: ViewGroup) : SimpleViewHolder<MedicinePicture>(R.layout.medicine_picture_item_view, parent) {
        init {
            addMedicinePicture.setOnClickListener { itemData?.let { onPictureClick(it) } }
        }

        override fun bind(item: MedicinePicture) {
            addMedicinePicture.setImageResource(item.imageResource)
            addMedicinePicture.setColorFilter(
                    ContextCompat.getColor(context!!, if (item.isSelected) R.color.yellow_green else R.color.gray),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    private class TakingTimeFooterHolder(parent: ViewGroup) : SimpleViewHolder<Unit>(R.layout.list_item_add_medicine_footer, parent) {
        init {
            itemView.setOnClickListener { }
        }
    }

    private inner class TakingTimeViewHolder(parent: ViewGroup) : SimpleViewHolder<TakingTime>(R.layout.time_item_view, parent) {
        override fun bind(item: TakingTime) {
            addTakingTime.apply {
                text = DateFormat.getTimeInstance(DateFormat.SHORT).format(item.takingTime)
                val calendar = Calendar.getInstance()
                calendar.time = item.takingTime
                setOnClickListener {
                    TimePickerDialog(
                            context, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            text = DateFormat.getTimeInstance(DateFormat.SHORT).format(this.time)
                        }
                    },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true)
                            .show()
                }
            }
            addTakingTimeDosage.setText(item.dosage.toString())
            val strings = resources.getStringArray(R.array.medicine_types).toList()
            val arrayAdapter = ArrayAdapter(context!!, R.layout.spinner_item, strings)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            addTakingTimeDosageSpinner.adapter = arrayAdapter
        }
    }

    private data class MedicinePicture(val imageResource: Int, var isSelected: Boolean)
    private data class TakingTime(val takingTime: Date, val dosage: Double)
    private data class AdapterWrapper<Adapter : RecyclerView.Adapter<*>, Data>(val adapter: Adapter, val reader: Adapter.() -> List<Data>, val writer: Adapter.(List<Data>) -> Unit) {
        var data: List<Data>
            get() = reader.invoke(adapter)
            set(value) = writer.invoke(adapter, value)
    }

    private data class FrequencyData(val category: FrequencyCategory,
                                     val labelProvider: (Frequency) -> String,
                                     private val adapterWrapper: AdapterWrapper<*, TakingTime>) {

        var adapter: RecyclerView.Adapter<*>
            private set

        init {
            adapter = adapterWrapper.adapter
        }

        var data: List<TakingTime>
            get() = adapterWrapper.data
            set(value) {
                adapterWrapper.data = value
            }
    }
}