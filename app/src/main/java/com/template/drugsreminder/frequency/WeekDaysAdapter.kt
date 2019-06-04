package com.template.drugsreminder.frequency

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.template.drugsreminder.R
import java.util.ArrayList


class WeekDaysAdapter(private val context: Context?) : RecyclerView.Adapter<WeekDaysAdapter.WeekDayViewHolder>() {

    private var items: List<String> = ArrayList()

    init {
        val resources = context?.resources
        items = resources?.getStringArray(R.array.week_days)?.toList() as List<String>
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): WeekDayViewHolder {
        return WeekDayViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.week_day_item_view,
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(viewHolder: WeekDayViewHolder, i: Int) {
        viewHolder.bind(items[i])
    }


    class WeekDayViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val weekDay = view.findViewById<CheckBox>(R.id.week_day_checkbox)

        fun bind(day: String) {
            weekDay.text = day
        }
    }
}

