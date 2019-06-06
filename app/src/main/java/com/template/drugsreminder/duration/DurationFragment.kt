package com.template.drugsreminder.duration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import android.app.DatePickerDialog
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.widget.RadioButton
import com.template.drugsreminder.models.Duration
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.observe
import kotlinx.android.synthetic.main.duration_till_date_layout.view.*
import kotlinx.android.synthetic.main.fragment_duration.*
import kotlinx.android.synthetic.main.fragment_frequency.*
import java.util.*


class DurationFragment : BaseFragment() {

    private lateinit var model: DurationViewModel

    private lateinit var adapter: SimpleRecyclerAdapter<DurationOption>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_duration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config.apply {
            isBottomBarVisible = false
            apply()
        }

        model = getViewModel(DurationViewModel::class)!!

        val data = listOf(
            DurationOption(
                Duration.WithoutDate,
                R.string.no_end_date
            ) { null },
            DurationOption(Duration.TillDate, R.string.till_date) { initTillDate(it) },
            DurationOption(Duration.DurationCount, R.string.duration) { initDurationCount(it) })

        durationOptionsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SimpleRecyclerAdapter(data, ::DurationOptionsHolder)
        durationOptionsList.adapter = adapter

        durationSaveBtn.setOnClickListener {
            model.saveDuration()
            getNavController().navigateUp()
        }

        model.duration.observe(this) { dur ->
            adapter.data.forEach { it.isSelected = it.option == dur }
            adapter.notifyDataSetChanged()
            durationDetailsContainer.removeAllViews()
            data.find { it.option == dur }?.handler?.invoke(durationDetailsContainer)
        }

    }


    private fun initTillDate(parent: ViewGroup): View {
        val v = LayoutInflater.from(context).inflate(R.layout.duration_till_date_layout, parent, true)
        v.durationTillDateLayout.setOnClickListener(this::onTillDateLayoutClick)
        return v
    }


    private fun initDurationCount(parent: ViewGroup) =
        LayoutInflater.from(context).inflate(R.layout.duration_count_layout, parent, true)

    private data class DurationOption(
        val option: Duration,
        @StringRes val titleRes: Int,
        var isSelected: Boolean = false,
        val handler: (ViewGroup) -> View?
    )


    private inner class DurationOptionsHolder(parent: ViewGroup) :
        SimpleViewHolder<DurationOption>(R.layout.radio_button_option_item_view, parent) {
        private var data: DurationOption? = null

        init {
            itemView.setOnClickListener { data?.option?.let { model.duration.value = it } }
        }

        override fun bind(data: DurationOption) {
            this.data = data
            (itemView as RadioButton).apply {
                isChecked = data.isSelected
                text = getString(data.titleRes)
            }
        }

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
