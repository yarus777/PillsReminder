package com.template.drugsreminder.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.template.drugsreminder.R
import com.template.drugsreminder.addmedicine.AddMedicineFragment
import com.template.drugsreminder.addmedicine.AddMedicineViewModel
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.duration.DurationFragment
import com.template.drugsreminder.models.MedicineModel
import com.template.drugsreminder.models.ScheduleMedicineModel
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.schedule_item_view.*
import java.util.*


class MainFragment : BaseFragment() {

    private lateinit var model: MainViewModel

    lateinit var scheduleListAdapter: SimpleRecyclerAdapter<ScheduleMedicineModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = getViewModel(MainViewModel::class)!!

        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        val pics = (0..pictures.length()).map { i -> pictures.getResourceId(i, 0) }
        pictures.recycle()
        model.pictures = pics

        mainMenuAddMedicine.setOnClickListener { getNavController().navigate(R.id.action_main_to_addMedicine) }

        val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val endDate = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }

        val calendar: HorizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendar)
            .range(startDate, endDate)
            .datesNumberOnScreen(7)
            .configure()
            .showTopText(false)
            .end()
            .build()

        readFile()

        scheduleList.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        calendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                scheduleListAdapter = SimpleRecyclerAdapter(model.selectData(date!!.time), ::ScheduleItemHolder)
                scheduleList.adapter = scheduleListAdapter
            }
        }

    }

    private val PERMISSION_CODE = 100

    private fun readFile() {
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
                model.deserializeData()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                model.deserializeData()
            }
        }
    }

    private inner class ScheduleItemHolder(parent: ViewGroup) :
        SimpleViewHolder<ScheduleMedicineModel>(R.layout.schedule_item_view, parent) {

        override fun bind(data: ScheduleMedicineModel) {
            medicineName.text = data.name
            medicinePicture.setImageResource(data.picture)
        }

    }

}