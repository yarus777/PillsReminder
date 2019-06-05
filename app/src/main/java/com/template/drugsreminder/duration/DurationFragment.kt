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
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.fragment_duration.*
import kotlinx.android.synthetic.main.fragment_frequency.*
import java.util.*


class DurationFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_duration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getConfig().setBottomBarVisible(false).apply()

        durationRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.till_date_radio_btn -> {
                    setLayoutsVisibility(false, true)
                }
                R.id.within_x_days_radio_btn -> {
                    setLayoutsVisibility(true, false)
                }
                else -> {
                    setLayoutsVisibility(false, false)
                }
            }
        }

        durationTillDateLayout.setOnClickListener(this::onTillDateLayoutClick)

        durationSaveBtn.setOnClickListener(this::onSaveBtnClick)

    }

    private fun onSaveBtnClick(v: View) {
        getNavController().navigateUp()
    }

    private fun setLayoutsVisibility(
        durationVisibility: Boolean,
        durationTillDateVisibility: Boolean
    ) {
        durationLayout.visibility = if (durationVisibility) View.VISIBLE else View.GONE
        durationTillDateLayout.visibility = if (durationTillDateVisibility) View.VISIBLE else View.GONE
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
