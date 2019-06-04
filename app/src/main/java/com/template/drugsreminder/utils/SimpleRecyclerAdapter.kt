package com.template.drugsreminder.utils

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

class SimpleRecyclerAdapter<Data>(var data: List<Data>, val viewHolderCreator: (ViewGroup) -> SimpleViewHolder<Data>) :
    RecyclerView.Adapter<SimpleViewHolder<Data>>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = viewHolderCreator(parent)
    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: SimpleViewHolder<Data>, position: Int) = holder.bind(data[position])
}

abstract class SimpleViewHolder<Data>(@LayoutRes viewId: Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(viewId, parent, false)), LayoutContainer {
    override val containerView = itemView

    abstract fun bind(data: Data)
}