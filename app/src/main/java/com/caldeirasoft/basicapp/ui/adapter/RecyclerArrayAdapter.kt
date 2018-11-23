package com.caldeirasoft.basicapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerArrayAdapter<T>(
        val context: Context
) : ListAdapter<T, RecyclerArrayAdapter.ViewHolder>(defaultItemDiffCallback<T>())
{
    private var itemViewClickListener: ItemViewClickListener<T>? = null
    private var itemViewSelectedListener: ItemViewSelectedListener<T>? = null

    protected var selectedIndex: Int = RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleView.isSelected = (position == selectedIndex)
    }

    override fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view:View = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false)
        return ViewHolder(view, this::onPositionClick)
    }

    protected open fun onPositionClick(position: Int, @IdRes viewId: Int) {
        setSelection(position)
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }

    fun setOnClickListener(listener: ItemViewClickListener<T>?){
        itemViewClickListener = listener
    }

    fun setOnSelectedListener(listener: ItemViewSelectedListener<T>?){
        itemViewSelectedListener = listener
    }

    fun setSelection(position: Int) {
        if (position == RecyclerView.NO_POSITION) {
            itemViewSelectedListener?.onItemSelected(null, position)
            return
        }

        notifyItemChanged(selectedIndex)
        selectedIndex = position
        notifyItemChanged(selectedIndex)
        itemViewSelectedListener?.onItemSelected(getItem(position), position)
    }

    class ViewHolder(itemView: View,
                         private val positionClick: (Int,Int) -> Unit) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleView: TextView
        init {
            titleView = itemView.findViewById(android.R.id.text1)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            positionClick.invoke(adapterPosition, v?.id ?: View.NO_ID)
        }
    }

}