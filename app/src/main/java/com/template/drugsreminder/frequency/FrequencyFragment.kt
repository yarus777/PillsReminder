package com.template.drugsreminder.frequency

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.addOnTextChangedListener
import com.template.drugsreminder.utils.observe
import kotlinx.android.synthetic.main.fragment_frequency.*
import kotlinx.android.synthetic.main.frequency_cycle_layout.*
import kotlinx.android.synthetic.main.frequency_every_x_day_layout.*
import kotlinx.android.synthetic.main.frequency_every_x_day_layout.view.*
import kotlinx.android.synthetic.main.frequency_every_x_hours_a_day_layout.*
import kotlinx.android.synthetic.main.frequency_every_x_hours_a_day_layout.frequencyEveryXHourADayEdit
import kotlinx.android.synthetic.main.frequency_every_x_hours_a_day_layout.view.*
import kotlinx.android.synthetic.main.frequency_week_days_layout.view.*
import kotlinx.android.synthetic.main.frequency_x_times_a_day_layout.*
import kotlinx.android.synthetic.main.frequency_x_times_a_day_layout.view.*
import kotlinx.android.synthetic.main.week_day_item_view.*

class FrequencyFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_frequency, container, false)
    }

    private lateinit var model: FrequencyViewModel

    private lateinit var adapter: SimpleRecyclerAdapter<FrequencyOption>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config.apply {
            isBottomBarVisible = false
            apply()
        }

        model = getViewModel(FrequencyViewModel::class)!!

        val data = listOf(
            FrequencyOption(TimesADay(1), R.string.x_times_a_day) { parent, item -> initTimesADay(parent, item) },
            FrequencyOption(HoursADay(1), R.string.every_x_hours_a_day) { parent, item -> initHoursADay(parent, item) },
            FrequencyOption(DaysAWeek(1), R.string.every_x_days) { parent, item -> initDaysAWeek(parent, item) },
            FrequencyOption(Weekly((1..5).toList()), R.string.certain_week_days) { parent, item ->
                initWeekly(
                    parent,
                    item
                )
            },
            FrequencyOption(Cycle(21, 7, 1), R.string.cycle) { parent, item -> initCycle(parent, item) }
        )
        frequencyOptionsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SimpleRecyclerAdapter(data, ::FrequencyOptionsHolder)
        frequencyOptionsList.adapter = adapter

        frequencySaveBtn.setOnClickListener {
            model.saveFrequency()
            getNavController().navigateUp()
        }

        model.frequency.observe(this) {
            val freq = it!!
            adapter.data.forEach { it.isSelected = it.option.category == freq.category }
            adapter.notifyDataSetChanged()
            frequencyDetailsContainer.removeAllViews()
            data.find { it.option.category == freq.category }?.handler?.invoke(frequencyDetailsContainer, freq)
        }
    }

    private fun initTimesADay(parent: ViewGroup, frequency: Frequency) =
        LayoutInflater.from(context).inflate(R.layout.frequency_x_times_a_day_layout, parent, true).apply {
            frequencyXTimesADayEdit.apply {
                setText((frequency as TimesADay).timesCount.toString())
                addOnTextChangedListener { raw ->
                    raw.takeIf { it.isNotBlank() }
                        ?.toInt()
                        ?.let { (model.frequency.value as TimesADay).timesCount = it }
                }
            }
        }


    private fun initDaysAWeek(parent: ViewGroup, frequency: Frequency) =
        LayoutInflater.from(context).inflate(R.layout.frequency_every_x_day_layout, parent, true).apply {
            frequencyEveryXDaysEdit.apply {
                setText((frequency as DaysAWeek).daysCount.toString())
                addOnTextChangedListener { raw ->
                    raw.takeIf { it.isNotBlank() }
                        ?.toInt()
                        ?.let { (model.frequency.value as DaysAWeek).daysCount = it }
                }
            }
        }


    private fun initHoursADay(parent: ViewGroup, frequency: Frequency) =
        LayoutInflater.from(context).inflate(R.layout.frequency_every_x_hours_a_day_layout, parent, true).apply {
            frequencyEveryXHourADayEdit.apply {
                setText((frequency as HoursADay).hoursCount.toString())
                addOnTextChangedListener { raw ->
                    raw.takeIf { it.isNotBlank() }
                        ?.toInt()
                        ?.let { (model.frequency.value as HoursADay).hoursCount = it }
                }
            }
        }

    private fun initWeekly(parent: ViewGroup, frequency: Frequency): View {
        val v = LayoutInflater.from(context).inflate(R.layout.frequency_week_days_layout, parent, true)
        v.frequencyWeekDaysList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val data = resources.getStringArray(R.array.week_days).toList()
        v.frequencyWeekDaysList.adapter = SimpleRecyclerAdapter(data, ::WeekDayViewHolder)
        return v
    }

    private fun initCycle(parent: ViewGroup, frequency: Frequency) =
        LayoutInflater.from(context).inflate(R.layout.frequency_cycle_layout, parent, true).apply {
            with(frequency as Cycle)
            {
                frequencyCycleActiveDaysEdit.apply {
                    setText(activeDaysCount.toString())
                    addOnTextChangedListener { raw ->
                        raw.takeIf { it.isNotBlank() }
                            ?.toInt()
                            ?.let { (model.frequency.value as Cycle).activeDaysCount = it }
                    }
                }
                frequencyCycleBreakDaysEdit.apply {
                    setText(breakDaysCount.toString())
                    addOnTextChangedListener { raw ->
                        raw.takeIf { it.isNotBlank() }
                            ?.toInt()
                            ?.let { (model.frequency.value as Cycle).breakDaysCount = it }
                    }
                }
                frequencyCycleDayEdit.apply {
                    setText(cycleDay.toString())
                    addOnTextChangedListener { raw ->
                        raw.takeIf { it.isNotBlank() }
                            ?.toInt()
                            ?.let { (model.frequency.value as Cycle).cycleDay = it }
                    }
                }

            }
        }


    private inner class FrequencyOptionsHolder(parent: ViewGroup) :
        SimpleViewHolder<FrequencyOption>(R.layout.radio_button_option_item_view, parent) {
        private var data: FrequencyOption? = null

        init {
            itemView.setOnClickListener { data?.option?.let { model.frequency.value = it } }
        }

        override fun bind(data: FrequencyOption) {
            this.data = data
            (itemView as RadioButton).apply {
                isChecked = data.isSelected
                text = getString(data.titleRes)
            }
        }

    }

    private data class FrequencyOption(
        val option: Frequency,
        @StringRes val titleRes: Int,
        var isSelected: Boolean = false,
        val handler: (ViewGroup, Frequency) -> View
    )


    class WeekDayViewHolder(parent: ViewGroup) : SimpleViewHolder<String>(R.layout.week_day_item_view, parent) {
        override fun bind(data: String) {
            weekDayCheckbox.text = data
        }
    }

}