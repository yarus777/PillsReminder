package com.template.drugsreminder.frequency

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        getConfig().setBottomBarVisible(false).apply()

        frequencyWeekDaysList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val data = resources.getStringArray(R.array.week_days).toList()
        frequencyWeekDaysList.adapter = SimpleRecyclerAdapter(data, ::WeekDayViewHolder)

        frequencyRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.frequencyCertainWeekDaysRadioBtn -> {
                    setLayoutsVisibility(true, false, false)
                }
                R.id.frequencyEveryXDaysRadioBtn -> {
                    setLayoutsVisibility(false, false, true)
                }
                R.id.frequencyCycleRadioBtn -> {
                    setLayoutsVisibility(false, true, false)
                }
                else -> {
                    setLayoutsVisibility(false, false, false)
                }
            }
        }

        frequencySaveBtn.setOnClickListener(this::onSaveBtnClick)
    }


    private fun onSaveBtnClick(v: View) {
        getNavController().navigateUp()
    }


    class WeekDayViewHolder(parent: ViewGroup) : SimpleViewHolder<String>(R.layout.week_day_item_view, parent) {
        override fun bind(data: String) {
            weekDayCheckbox.text = data
        }
    }

    private fun setLayoutsVisibility(
        weekDaysVisibility: Boolean,
        cycleDaysVisibility: Boolean,
        everyDayVisibility: Boolean
    ) {
        frequencyWeekDaysLayout.visibility = if (weekDaysVisibility) View.VISIBLE else View.GONE
        frequencyCycleDaysLayout.visibility = if (cycleDaysVisibility) View.VISIBLE else View.GONE
        frequencyEveryDayLayout.visibility = if (everyDayVisibility) View.VISIBLE else View.GONE
    }
}