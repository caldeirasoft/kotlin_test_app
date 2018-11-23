package com.caldeirasoft.basicapp.ui.filter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.MenuRes
import androidx.databinding.ViewDataBinding
import com.caldeirasoft.basicapp.ui.adapter.ChipAdapter
import com.caldeirasoft.basicapp.ui.adapter.ItemViewSelectedListener
import com.google.android.material.chip.Chip

abstract class MenuFilterFragment<B: ViewDataBinding> : BaseFilterFragment<String, B>(), ItemViewSelectedListener<String>
{
    protected abstract val menuRes: Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setItems(menuRes)
        //arrayAdapter.setOnSelectedListener(this)
    }

    fun setItems(@MenuRes menuRes: Int) {
        val dummyMenu = PopupMenu(context, null)
        val menu = dummyMenu.menu
        MenuInflater(context).inflate(menuRes, menu)

        val items = mutableListOf<String>()
        val icons = mutableListOf<Drawable?>()
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.isEnabled) {
                items.add(item.title.toString())
                icons.add(item.icon)
            }
        }
        setItems(items, icons)
    }

    fun setChipAdapterItems(chipAdapter: ChipAdapter<String>, @MenuRes menuRes: Int) {
        val dummyMenu = PopupMenu(chipAdapter.context, null)
        val menu = dummyMenu.menu
        MenuInflater(chipAdapter.context).inflate(menuRes, menu)

        val items = mutableListOf<String>()
        val icons = mutableListOf<Drawable?>()
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.isEnabled) {
                items.add(item.title.toString())
                icons.add(item.icon)
            }
        }

        chipAdapter.submitList(items.toList())
    }

    override fun onBindItemViewHolder(item: String?, holder: ChipAdapter.ViewHolder, drawable:Drawable?) {
        holder.chipView.apply {
            text = item
            chipIcon = drawable
        }
    }

    override fun onItemSelected(item: String?, position: Int) {
        when (position) {
            0 -> {
                this.view?.findViewById<Chip>(getChipId())?.visibility = View.GONE
                onCloseListener?.onClick(this.view)
            }
            else -> {
                val selectedChip = this.view?.findViewById<Chip>(getChipId())
                selectedChip?.let {
                    it.visibility = View.VISIBLE
                    it.text = item
                    it.chipIcon = getDrawable(position)
                }
            }
        }
    }
}