package com.template.drugsreminder.takingtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_taking_time.*


class TakingTimeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_taking_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getConfig().setBottomBarVisible(false).apply()

        takingTimeSaveBtn.setOnClickListener(this::onSaveBtnClick)

    }

    private fun onSaveBtnClick(v: View) {
        getNavController().navigateUp()
    }
}