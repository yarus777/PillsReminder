package com.template.drugsreminder.addmedicine

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.medicine_picture_item_view.*


class AddMedicineFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getConfig().setBottomBarVisible(false).apply()

        addMedicineDurationLayout.setOnClickListener(this::onDurationClick)
        addMedicineFrequencyLayout.setOnClickListener(this::onFrequencyClick)
        addMedicineTakingTimeLayout.setOnClickListener(this::onTakingTimeClick)

        addMedicinePictureList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addMedicinePictureList.adapter = SimpleRecyclerAdapter(prepareAdapterData(), ::MedicinePictureViewHolder)

        addMedicineSaveBtn.setOnClickListener(this::onSaveBtnClick)
    }

    private fun onSaveBtnClick(v: View) {
        getNavController().navigateUp()
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

    class MedicinePictureViewHolder(parent: ViewGroup) :
        SimpleViewHolder<MedicinePicture>(R.layout.medicine_picture_item_view, parent) {
        override fun bind(data: MedicinePicture) {
            addMedicinePicture.setImageResource(data.imageResource)
            addMedicinePicture.setOnClickListener(this::onPictureClick)
        }

        private fun onPictureClick(v: View) {
            addMedicinePicture.setColorFilter(
                ContextCompat.getColor(v.context, R.color.yellow_green),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun prepareAdapterData(): ArrayList<MedicinePicture> {
        val data = ArrayList<MedicinePicture>()
        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        for (i in 0 until pictures.length()) {
            data.add(MedicinePicture(pictures.getResourceId(i, 0), false))
        }
        pictures.recycle()
        return data
    }

    class MedicinePicture(val imageResource: Int, isSelected: Boolean) {
    }
}