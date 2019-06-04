package com.template.drugsreminder.addmedicine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment

class AddMedicineFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<LinearLayout>(R.id.duration_layout)
            .setOnClickListener(this::onDurationClick)

        view.findViewById<LinearLayout>(R.id.frequency_layout)
            .setOnClickListener(this::onFrequencyClick)

        view.findViewById<LinearLayout>(R.id.taking_time_layout)
            .setOnClickListener(this::onTakingTimeClick)
    }

    private fun onDurationClick(v: View) {
        getNavController().navigate(R.id.action_addMedicine_to_duration)
    }

    private fun onFrequencyClick(v: View) {
        getNavController().navigate(R.id.action_addMedicine_to_frequency)
    }

    private fun onTakingTimeClick(v: View) {
        getNavController().navigate(R.id.action_addMedicine_to_takingTime)
    }
}