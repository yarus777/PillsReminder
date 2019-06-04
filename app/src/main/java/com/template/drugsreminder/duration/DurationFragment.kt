package com.template.drugsreminder.duration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import android.app.DatePickerDialog
import android.widget.RadioButton
import java.util.*


class DurationFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_duration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tillDateLayout = view.findViewById<LinearLayout>(R.id.till_date_layout)
        val durationLayout = view.findViewById<LinearLayout>(R.id.duration_layout)

        view.findViewById<RadioGroup>(R.id.duration_radio_group)
            .setOnCheckedChangeListener { group, checkedId ->

                when (checkedId) {
                    R.id.till_date_radio_btn -> {
                        tillDateLayout.visibility = View.VISIBLE
                        durationLayout.visibility = View.GONE
                    }
                    R.id.within_x_days_radio_btn -> {
                        durationLayout.visibility = View.VISIBLE
                        tillDateLayout.visibility = View.GONE
                    }
                    else -> {
                        durationLayout.visibility = View.GONE
                        tillDateLayout.visibility = View.GONE
                    }
                }
            }

        tillDateLayout.setOnClickListener(this::onTillDateLayoutClick)

    }

    private fun onTillDateLayoutClick(v: View) {
        val dateAndTime = Calendar.getInstance()

        DatePickerDialog(
            context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateAndTime.set(Calendar.YEAR, year)
                dateAndTime.set(Calendar.MONTH, monthOfYear)
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            },
            dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
}
