package com.caldeirasoft.basicapp.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.R
import com.google.android.material.chip.Chip

abstract class ChipAdapter<T>(
        val context: Context
) : ListAdapter<T, ChipAdapter.ViewHolder>(defaultItemDiffCallback<T>())
{
    private var itemViewClickListener: ItemViewClickListener<T>? = null
    private var itemViewSelectedListener: ItemViewSelectedListener<T>? = null

    public var selectedIndex: Int = 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isChecked = (position == selectedIndex)
        setChipStyle(holder.chipView, isChecked)
    }

    override public fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view:View = inflater.inflate(R.layout.listitem_chip, parent, false)
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

        if (position == selectedIndex) {
            return
        }

        notifyItemChanged(selectedIndex)
        selectedIndex = position
        notifyItemChanged(selectedIndex)
        itemViewSelectedListener?.onItemSelected(getItem(position), position)
    }

    fun setChipStyle(chip: Chip, isSelected: Boolean) {
        if (isSelected) {
            chip.isCheckable = true
            chip.isCheckedIconEnabled = true
            chip.isChipIconEnabled = false
            chip.isChecked = true
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.chip_selected_background_color))
            chip.chipStrokeWidth = 0f
        }
        else {
            chip.isCheckable = false
            chip.isCheckedIconEnabled = false
            chip.isChipIconEnabled = true
            chip.isChecked = false
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.context, android.R.color.transparent))
            chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.chip_selected_background_color))
            chip.chipStrokeWidth = 2f
        }
    }

    class ViewHolder(itemView: View,
                     private val positionClick: (Int,Int) -> Unit) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val chipView: Chip
        init {
            chipView = itemView.findViewById(R.id.chip1)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            positionClick.invoke(adapterPosition, v?.id ?: View.NO_ID)
        }
    }
}
