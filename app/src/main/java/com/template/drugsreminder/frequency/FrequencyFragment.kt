package com.template.drugsreminder.frequency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment

class FrequencyFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_frequency, container, false)
    }
}