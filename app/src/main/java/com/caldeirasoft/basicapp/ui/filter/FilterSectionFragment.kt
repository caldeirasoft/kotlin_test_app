package com.caldeirasoft.basicapp.ui.filter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.RecyclerArrayAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.GridDividerDecoration
import com.caldeirasoft.basicapp.ui.base.BaseFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_filter_section.*

class FilterSectionFragment() : BaseFragment() {

    private var menu: Menu? = null

    override fun getLayout() = R.layout.bottom_sheet_filter_section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreate() {
    }

    fun setItems(@MenuRes menuRes:Int, listener: ItemViewClickListener<String>?) {
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
        setItems(items.toTypedArray(), icons.toTypedArray(), listener)
    }

    fun setItems(items:Array<String>, drawables:Array<Drawable?>, listener: ItemViewClickListener<String>?) {
        val arrayAdapter = object : RecyclerArrayAdapter<String>(this.context!!) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                val item = getItem(position)
                holder.titleView.apply {
                    text = item
                    textSize = 12f
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                //val d = AppCompatResources.getDrawable(context, resId[position])
                drawables[position]?.apply {
                    context.resources.getDimensionPixelSize(R.dimen.navigation_large_icon_bounds).let {
                        setBounds(0, 0, it, it)
                    }
                    holder.titleView.compoundDrawablePadding = 10
                    holder.titleView.setCompoundDrawables(null, this, null, null)
                }
            }
        }

        recyclerView_filter_section.let {
            //it.layoutManager = GridLayoutManager(activity, SPAN_NUMBER)
            //it.addItemDecoration(GridDividerDecoration(requireContext()))
            //it.adapter = arrayAdapter
        }
    }

    fun getAdapter(@MenuRes menuRes:Int): RecyclerArrayAdapter<String> {
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
        return getAdapter(items.toTypedArray(), icons.toTypedArray())
    }

    fun getAdapter(items:Array<String>, drawables:Array<Drawable?>): RecyclerArrayAdapter<String> {
        val arrayAdapter = object : RecyclerArrayAdapter<String>(this.context!!) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

                val item = getItem(position)
                holder.titleView.apply {
                    text = item
                    textSize = 12f
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                //val d = AppCompatResources.getDrawable(context, resId[position])
                drawables[position]?.apply {
                    context.resources.getDimensionPixelSize(R.dimen.navigation_large_icon_bounds).let {
                        setBounds(0, 0, it, it)
                    }
                    holder.titleView.compoundDrawablePadding = 10
                    holder.titleView.setCompoundDrawables(null, this, null, null)
                }
            }
        }
        return arrayAdapter
    }

    private companion object {
        const val SPAN_NUMBER = 4
    }
}