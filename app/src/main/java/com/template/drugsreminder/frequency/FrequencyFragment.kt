package com.template.drugsreminder.frequency

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment

class FrequencyFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_frequency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weekDaysRecycler = view.findViewById<RecyclerView>(R.id.week_days_recycler_view)
        weekDaysRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val weekDaysAdapter = WeekDaysAdapter(context)
        weekDaysRecycler.adapter = weekDaysAdapter

        val weekDaysLayout = view.findViewById<LinearLayout>(R.id.week_days_layout)

        view.findViewById<RadioGroup>(R.id.frequency_radio_group)
            .setOnCheckedChangeListener { group, checkedId ->

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
}