package com.template.drugsreminder.frequency

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import kotlinx.android.synthetic.main.fragment_frequency.*
import kotlinx.android.synthetic.main.week_day_item_view.*

class FrequencyFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_frequency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        frequencyWeekDaysList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val data = resources.getStringArray(R.array.week_days).toList()
        frequencyWeekDaysList.adapter = SimpleRecyclerAdapter(data, ::WeekDayViewHolder)

        val weekDaysLayout = view.findViewById<LinearLayout>(R.id.week_days_layout)

        frequency_radio_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.certain_week_days_radio_btn -> {
                    weekDaysLayout.visibility = View.VISIBLE
                }
                R.id.every_x_days_radio_btn -> {
                    weekDaysLayout.visibility = View.GONE
                }
                R.id.cycle_radio_btn -> {
                    weekDaysLayout.visibility = View.GONE
                }
                else -> {
                    weekDaysLayout.visibility = View.GONE
                }
            }
        }
    }

    class WeekDayViewHolder(parent: ViewGroup) : SimpleViewHolder<String>(R.layout.week_day_item_view, parent) {
        override fun bind(data: String) {
            weekDayCheckbox.text = data
        }
    }
}