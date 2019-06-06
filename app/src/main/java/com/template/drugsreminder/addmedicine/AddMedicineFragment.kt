package com.template.drugsreminder.addmedicine

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.template.drugsreminder.R
import com.template.drugsreminder.base.BaseFragment
import com.template.drugsreminder.duration.DurationViewModel
import com.template.drugsreminder.frequency.FrequencyViewModel
import com.template.drugsreminder.utils.SimpleRecyclerAdapter
import com.template.drugsreminder.utils.SimpleViewHolder
import com.template.drugsreminder.utils.observe
import kotlinx.android.synthetic.main.fragment_add_medicine.*
import kotlinx.android.synthetic.main.medicine_picture_item_view.*

class AddMedicineFragment : BaseFragment() {
    private lateinit var adapter: SimpleRecyclerAdapter<MedicinePicture>

    private lateinit var model: AddMedicineViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_medicine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        config.apply {
            isBottomBarVisible = false
            apply()
        }

        model = getViewModel(AddMedicineViewModel::class)!!

        addMedicineDurationLayout.setOnClickListener {
            getNavController().navigate(
                R.id.action_addMedicine_to_duration,
                DurationViewModel(model.duration)
            )
        }
        addMedicineFrequencyLayout.setOnClickListener {
            getNavController().navigate(
                R.id.action_addMedicine_to_frequency,
                FrequencyViewModel(model.frequency)
            )
        }

        addMedicinePictureList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = SimpleRecyclerAdapter(prepareAdapterData(), ::MedicinePictureViewHolder)
        addMedicinePictureList.adapter = adapter

        addMedicineSaveBtn.setOnClickListener { getNavController().navigateUp() }

        model.frequency.observe(this) { addMedicineFrequencyValue.text = ""}
        model.duration.observe(this) { addMedicineDurationValue.text = "" }
    }

    private fun prepareAdapterData(): List<MedicinePicture> {
        val pictures = resources.obtainTypedArray(R.array.medicine_pictures)
        val data = (0..pictures.length()).map { i -> MedicinePicture(pictures.getResourceId(i, 0), false) }
        pictures.recycle()
        return data
    }

    private fun onPictureClick(item: MedicinePicture) {
        adapter.data.forEach { it.isSelected = item == it }
        adapter.notifyDataSetChanged()
    }

    private inner class MedicinePictureViewHolder(parent: ViewGroup) :
        SimpleViewHolder<MedicinePicture>(R.layout.medicine_picture_item_view, parent) {

        private var data: MedicinePicture? = null

        init {
            addMedicinePicture.setOnClickListener { data?.let { onPictureClick(it) } }
        }

        override fun bind(data: MedicinePicture) {
            this.data = data
            addMedicinePicture.setImageResource(data.imageResource)
            addMedicinePicture.setColorFilter(
                ContextCompat.getColor(context!!, if (data.isSelected) R.color.yellow_green else R.color.gray),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    private data class MedicinePicture(val imageResource: Int, var isSelected: Boolean)
}