package com.template.drugsreminder.addmedicine

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.duration.DurationViewModel
import com.template.drugsreminder.frequency.FrequencyViewModel
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.observe
import kotlinx.android.synthetic.main.duration_till_date_layout.view.*
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.medicine_picture_item_view.*
import kotlinx.android.synthetic.main.time_item_view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddMedicineFragment : BaseFragment() {
    private lateinit var adapter: SimpleRecyclerAdapter<MedicinePicture>

    private lateinit var model: AddMedicineViewModel

    private lateinit var takingTimeAdapter: SimpleRecyclerAdapter<TakingTime>

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
            getNavController().navigate(
                R.id.action_addMedicine_to_duration,
                DurationViewModel(model.duration, model.startDate)
            )
        }
        addMedicineFrequencyLayout.setOnClickListener {
            getNavController().navigate(
                R.id.action_addMedicine_to_frequency,
                FrequencyViewModel(model.frequency)
            )
        }

        addMedicinePictureList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = SimpleRecyclerAdapter(prepareAdapterData(), ::MedicinePictureViewHolder)
        addMedicinePictureList.adapter = adapter

        addMedicineTakingTimeList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        addMedicineSaveBtn.setOnClickListener { getNavController().navigateUp() }

        model.frequency.observe(this) {
            addMedicineFrequencyValue.text = when (it) {
                is TimesADay -> {
                    setAddTakingTime(false)
                    resources.getString(R.string.x_times_a_day_pattern, it.timesCount)
                }
                is HoursADay -> {
                    setAddTakingTime(false)
                    resources.getString(R.string.every_x_hours_a_day_pattern, it.hoursCount)
                }
                is DaysAWeek -> {
                    setAddTakingTime(true)
                    resources.getString(R.string.every_x_days_pattern, it.daysCount)
                }
                is Weekly -> {
                    setAddTakingTime(true)
                    val strings = resources.getStringArray(R.array.week_days).toList()
                    it.weekDays.sortedBy { it }.joinToString(", ") { day -> strings[day] }
                }
                is Cycle -> {
                    setAddTakingTime(true)
                    resources.getString(R.string.cycle_pattern, it.activeDaysCount, it.breakDaysCount)
                }
                else -> ""
            }
            takingTimeAdapter = SimpleRecyclerAdapter(prepareTakingTimeAdapterData(it), ::AddTakingTimeViewHolder)
            addMedicineTakingTimeList.adapter = takingTimeAdapter
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

    private fun setAddTakingTime(isVisible: Boolean) {
        addMedicineTakingTime.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun prepareAdapterData(): List<MedicinePicture> {
        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        val data = (0..pictures.length()).map { i -> MedicinePicture(pictures.getResourceId(i, 0), false) }
        pictures.recycle()
        return data
    }

    private fun prepareTakingTimeAdapterData(frequency: Frequency?) = when (frequency) {
        is TimesADay -> {
            val interval = (24f * 60 / frequency.timesCount).roundToInt()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 24)
                set(Calendar.MINUTE, 0)
            }
            (0 until frequency.timesCount).map { i ->
                TakingTime((calendar.clone() as Calendar).apply {
                    add(
                        Calendar.MINUTE,
                        i * interval
                    )
                }.time, 1.0)
            }
        }
        else -> ArrayList<TakingTime>()
    }


    private fun onPictureClick(item: MedicinePicture) {
        adapter.data.forEach { it.isSelected = item == it }
        adapter.notifyDataSetChanged()
    }

    private inner class MedicinePictureViewHolder(parent: ViewGroup) :
        SimpleViewHolder<MedicinePicture>(R.layout.medicine_picture_item_view, parent) {

        private var data: MedicinePicture? = null

        init {
            addMedicinePicture.setOnClickListener { data?.let { onPictureClick(it) } }
        }

        override fun bind(data: MedicinePicture) {
            this.data = data
            addMedicinePicture.setImageResource(data.imageResource)
            addMedicinePicture.setColorFilter(
                ContextCompat.getColor(context!!, if (data.isSelected) R.color.yellow_green else R.color.gray),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    private data class MedicinePicture(val imageResource: Int, var isSelected: Boolean)

    private inner class AddTakingTimeViewHolder(parent: ViewGroup) :
        SimpleViewHolder<TakingTime>(R.layout.time_item_view, parent) {
        override fun bind(data: TakingTime) {
            addTakingTime.apply {
                text = DateFormat.getTimeInstance(DateFormat.SHORT).format(data.takingTime)
                val calendar = Calendar.getInstance()
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
                        true
                    )
                        .show()
                }
            }
            addTakingTimeDosage.setText(data.dosage.toString())
            val strings = resources.getStringArray(R.array.medicine_types).toList()
            val arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, strings)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addTakingTimeDosageSpinner.adapter = arrayAdapter
        }
    }

    private data class TakingTime(val takingTime: Date, val dosage: Double)
}