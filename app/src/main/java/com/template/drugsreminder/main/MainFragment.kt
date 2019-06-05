package com.template.drugsreminder.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


class MainFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainMenuAddMedicine.setOnClickListener(this::onAddMedicineButtonClick)

        val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val endDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }

        val calendar: HorizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendar)
            .range(startDate, endDate)
            .datesNumberOnScreen(7)
            .configure()
                .showTopText(false)
            .end()
            .build()

        calendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
            }
        }
    }

    private fun onAddMedicineButtonClick(v: View) {
        getNavController().navigate(R.id.action_main_to_addMedicine)
    }
}