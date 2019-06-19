package com.template.drugsreminder.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.models.ScheduleMedicineModel
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.observe
import com.template.drugsreminder.utils.resetToStartOfDay
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.schedule_item_view.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainFragment : BaseFragment() {
    companion object {
        private const val PERMISSION_CODE = 100
    }

    private lateinit var model: MainViewModel

    private lateinit var scheduleListAdapter: SimpleRecyclerAdapter<ScheduleMedicineModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = getViewModel(MainViewModel::class)!!

        checkPermissions()

        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        model.pictures = (0..pictures.length()).map { i -> pictures.getResourceId(i, 0) }
        pictures.recycle()

        mainMenuAddMedicine.setOnClickListener { getNavController().navigate(R.id.action_main_to_addMedicine) }

        HorizontalCalendar.Builder(view, R.id.calendar)
            .range(
                Calendar.getInstance().apply { add(Calendar.MONTH, -1) },
                Calendar.getInstance().apply { add(Calendar.MONTH, 1) })
            .datesNumberOnScreen(7)
            .configure()
            .showTopText(false)
            .end()
            .build().apply {
                calendarListener = object : HorizontalCalendarListener() {
                    override fun onDateSelected(date: Calendar?, position: Int) {
                        model.selectDate(date!!.time.resetToStartOfDay())
                    }
                }
            }

        model.selectDate(Calendar.getInstance().time.resetToStartOfDay())

        scheduleList.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        scheduleListAdapter = SimpleRecyclerAdapter(ArrayList(), ::ScheduleItemHolder)
        scheduleList.adapter = scheduleListAdapter

        model.data.observe(this) {
            if (it != null) scheduleListAdapter.data = it
            scheduleListAdapter.notifyDataSetChanged()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            } else {
                model.load()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                model.load()
            }
        }
    }

    private inner class ScheduleItemHolder(parent: ViewGroup) :
        SimpleViewHolder<ScheduleMedicineModel>(R.layout.schedule_item_view, parent) {

        override fun bind(data: ScheduleMedicineModel) {
            medicineName.text = data.name
            medicinePicture.setImageResource(data.picture)
            medicineTakingTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(data.time)
            medicineDosage.text = data.dosage.toString()
        }

    }

}