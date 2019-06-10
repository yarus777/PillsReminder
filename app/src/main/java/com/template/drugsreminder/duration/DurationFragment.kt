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
import com.template.drugsreminder.models.*
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.addOnTextChangedListener
import com.template.drugsreminder.utils.observe
import kotlinx.android.synthetic.main.duration_count_layout.view.*
import kotlinx.android.synthetic.main.duration_till_date_layout.view.*
import kotlinx.android.synthetic.main.fragment_duration.*
import kotlinx.android.synthetic.main.fragment_frequency.*
import java.text.DateFormat
import java.time.LocalDate
import java.time.Year
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
            DurationOption(WithoutDate(), R.string.no_end_date) { parent, item -> null },
            DurationOption(TillDate(Calendar.getInstance().time), R.string.till_date) { parent, item ->
                initTillDate(
                    parent,
                    item
                )
            },
            DurationOption(DurationCount(10), R.string.duration) { parent, item -> initDurationCount(parent, item) })

        durationOptionsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = SimpleRecyclerAdapter(data, ::DurationOptionsHolder)
        durationOptionsList.adapter = adapter

        durationSaveBtn.setOnClickListener {
            model.saveDuration()
            getNavController().navigateUp()
        }

        durationStartDateLayout.setOnClickListener{
            val calendar = Calendar.getInstance()
            calendar.time = model.startDate.value

            DatePickerDialog(
                context!!, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    model.startDate.value = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, monthOfYear)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.time
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        model.startDate.observe(this) {
            durationStartDate.text = DateFormat.getDateInstance(DateFormat.SHORT).format(it)
        }

        model.duration.observe(this) {
            val dur = it!!
            adapter.data.forEach { it.isSelected = it.option.category == dur.category }
            adapter.notifyDataSetChanged()
            durationDetailsContainer.removeAllViews()
            data.find { it.option.category == dur.category }?.handler?.invoke(durationDetailsContainer, dur)
        }

    }


    private fun initTillDate(parent: ViewGroup, duration: Duration) =
        LayoutInflater.from(context).inflate(R.layout.duration_till_date_layout, parent, true).apply {
            durationTillDateLayout.setOnClickListener {
                val calendar = Calendar.getInstance()
                calendar.time = (duration as TillDate).date

                DatePickerDialog(
                    context, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        (model.duration.value as TillDate).date = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, monthOfYear)
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            durationTillDate.text = DateFormat.getDateInstance(DateFormat.SHORT).format(this.time)
                        }.time
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                    .show()
            }
            durationTillDate.text = DateFormat.getDateInstance(DateFormat.SHORT).format((duration as TillDate).date)
        }


    private fun initDurationCount(parent: ViewGroup, duration: Duration) =
        LayoutInflater.from(context).inflate(R.layout.duration_count_layout, parent, true).apply {
            durationEdit.apply {
                setText((duration as DurationCount).durationCount.toString())
                addOnTextChangedListener { raw ->
                    raw.takeIf { it.isNotBlank() }
                        ?.toInt()
                        ?.let { (model.duration.value as DurationCount).durationCount = it }
                }
            }
        }

    private data class DurationOption(
        val option: Duration,
        @StringRes val titleRes: Int,
        var isSelected: Boolean = false,
        val handler: (ViewGroup, Duration) -> View?
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
}
