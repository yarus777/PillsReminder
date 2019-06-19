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

    private lateinit var iconsAdapter: SimpleRecyclerViewAdapter<MedicinePicture>

    private val fixedLengthAdapter =
        SimpleRecyclerViewAdapter(ArrayList(), { hashCode().toLong() }, ::TakingTimeViewHolder)
    private val variableLengthAdapter = HeaderFooterRecyclerViewAdapter(
        ArrayList(),
        { hashCode().toLong() },
        null,
        ::TakingTimeFooterHolder,
        ::TakingTimeViewHolder
    )

    private val frequenciesData = arrayListOf(
        FrequencyData(FrequencyCategory.TimesADay) {
            resources.getString(
                R.string.x_times_a_day_pattern,
                (it as TimesADay).timesCount
            )
        },
        FrequencyData(FrequencyCategory.HoursADay) {
            resources.getString(
                R.string.every_x_hours_a_day_pattern,
                (it as HoursADay).hoursCount
            )
        },
        FrequencyData(FrequencyCategory.DaysAWeek) {
            resources.getString(
                R.string.every_x_days_pattern,
                (it as DaysAWeek).daysCount
            )
        },
        FrequencyData(FrequencyCategory.Weekly) {
            val strings = resources.getStringArray(R.array.week_days).toList()
            (it as Weekly).weekDays.sortedBy { it }.joinToString(", ") { day -> strings[day.code] }
        },
        FrequencyData(FrequencyCategory.Cycle) {
            (it as Cycle).let {
                resources.getString(
                    R.string.cycle_pattern,
                    it.activeDaysCount,
                    it.breakDaysCount
                )
            }
        })

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
            getNavController().navigate(R.id.action_addMedicine_to_frequency, FrequencyViewModel(model.frequency))
        }

        addMedicinePictureList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        iconsAdapter = SimpleRecyclerViewAdapter(
            preparePicturesAdapterData(),
            { this.picture.id.toLong() },
            ::MedicinePictureViewHolder
        )
        addMedicinePictureList.adapter = iconsAdapter

        addMedicineTakingTimeList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        addMedicineSaveBtn.setOnClickListener {
            model.save()
            getNavController().navigateUp()
        }

        addMedicineName.addOnTextChangedListener {
            model.medicineName.value = it
        }

        model.medicineName.observe(this) {
            addMedicineSaveBtn.isEnabled = it!!.isNotEmpty()
        }

        model.frequency.observe(this) { frequency ->
            val data = frequency?.let { frequenciesData.first { data -> data.category == it.category } }
            addMedicineFrequencyValue.text = data?.labelProvider?.invoke(frequency)
            model.takingTimes.value = if (frequency?.fixedSize == true)
                AddMedicineViewModel.TakingTimes.fixed(prepareTakingTimeAdapterData(frequency))
            else AddMedicineViewModel.TakingTimes.float(1)
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

        model.takingTimes.observe(this) { data ->
            addMedicineTakingTimeList.adapter = data?.let {
                if (it.editable) variableLengthAdapter.apply { this.data = data.list }
                else fixedLengthAdapter.apply { this.data = data.list }
            }
        }
        model.selectedIcon.observe(this) {
            iconsAdapter.notifyDataSetChanged()
        }
    }

    private fun preparePicturesAdapterData(): List<MedicinePicture> {
        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        val data = model.icons.map { MedicinePicture(pictures.getResourceId(it.id, 0), it) }
        pictures.recycle()
        return data
    }

    private fun prepareTakingTimeAdapterData(frequency: Frequency?) = when (frequency) {
        is TimesADay -> {
            calculateEveryXTimesInterval(frequency.timesCount)
        }
        is HoursADay -> calculateEveryXHourIntervals(
            frequency.hoursCount,
            Calendar.getInstance().resetToStartOfDay().time
        )
        else -> ArrayList()
    }

    private fun calculateEveryXTimesInterval(timesCount: Int): List<TakingTime> {
        val interval = (24f * 60 / timesCount).roundToInt()
        val calendar = Calendar.getInstance().resetToStartOfDay()

        return (0 until timesCount).map { i ->
            TakingTime((calendar.clone() as Calendar).apply {
                add(Calendar.MINUTE, i * interval)
            }.time, 1.0)
        }
    }

    private fun calculateEveryXHourIntervals(hoursCount: Int, startHour: Date): List<TakingTime> {
        val list: ArrayList<TakingTime> = ArrayList()
        val calendar = Calendar.getInstance().apply {
            time = startHour
        }
        val nextDay = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            resetToStartOfDay()
        }
        do {
            list.add(TakingTime(calendar.time, 1.0))
        } while (calendar.apply { add(Calendar.HOUR_OF_DAY, hoursCount) }.time < nextDay.time)

        return list
    }

    private inner class MedicinePictureViewHolder(parent: ViewGroup) :
        SimpleViewHolder<MedicinePicture>(R.layout.medicine_picture_item_view, parent) {
        init {
            addMedicinePicture.setOnClickListener { if (itemData != null) model.selectIcon(itemData!!.picture) }
        }

        override fun bind(item: MedicinePicture) {
            addMedicinePicture.apply {
                setImageResource(item.imageResource)
                setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        if (model.selectedIcon.value?.equals(item.picture) == true) R.color.yellow_green else R.color.gray
                    ),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private inner class TakingTimeFooterHolder(parent: ViewGroup) :
        SimpleViewHolder<Unit>(R.layout.list_item_add_medicine_footer, parent) {
        init {
            itemView.setOnClickListener { model.addEmptyTimeToSchedule() }
        }
    }

    private inner class TakingTimeViewHolder(parent: ViewGroup) :
        SimpleViewHolder<TakingTime>(R.layout.time_item_view, parent) {
        override fun bind(item: TakingTime) {
            deleteTakingTime.apply {
                visibility =
                    if (model.takingTimes.value?.editable == true && adapterPosition != 0) View.VISIBLE else View.INVISIBLE
                setOnClickListener { model.removeTimeFromSchedule(item) }
            }

            addTakingTime.apply {
                text = DateFormat.getTimeInstance(DateFormat.SHORT).format(item.takingTime)
                Calendar.getInstance().apply {
                    time = item.takingTime
                    setOnClickListener {
                        TimePickerDialog(
                            context, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                                model.takingTimes.value?.list?.get(adapterPosition)?.takingTime =
                                    Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, hour)
                                        set(Calendar.MINUTE, minute)
                                        text = DateFormat.getTimeInstance(DateFormat.SHORT).format(this.time)
                                    }.time

                            },
                            get(Calendar.HOUR_OF_DAY),
                            get(Calendar.MINUTE),
                            true
                        )
                            .show()
                    }
                }
            }
            addTakingTimeDosage.apply {
                setText(item.dosage.toString())
                addOnTextChangedListener {
                    model.takingTimes.value?.list?.get(adapterPosition)?.dosage = it.toDouble()
                }

            }
            val strings = resources.getStringArray(R.array.medicine_types).toList()
            val arrayAdapter = ArrayAdapter(context!!, R.layout.spinner_item, strings)
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            addTakingTimeDosageSpinner.adapter = arrayAdapter
        }
    }

    private data class MedicinePicture(val imageResource: Int, val picture: AddMedicineViewModel.MedicineIcon)

    private data class FrequencyData(val category: FrequencyCategory, val labelProvider: (Frequency) -> String)
}