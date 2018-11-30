package com.caldeirasoft.basicapp.ui.common

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.caldeirasoft.basicapp.R
import com.google.android.material.bottomsheet.BottomSheetDialog


class BottomSheetDialogBuilder(mContext: Context, private val mIsGrid: Boolean)
{
    private val mDialog = BottomSheetDialog(mContext)
    private var mItems: Array<String> = arrayOf()
    private val mIconTint: Int = 0

    init {
        val listView = ListView(mContext).apply {
            id = android.R.id.list
            setPadding(0, 8, 0, 8)
        }

        mDialog.setContentView(listView)
    }

    fun setTitle(title: CharSequence): BottomSheetDialogBuilder {
        mDialog.setTitle(title)
        return this
    }

    fun setTitleId(titleId: Int): BottomSheetDialogBuilder {
        mDialog.setTitle(titleId)
        return this
    }

    fun setCancelable(cancelable: Boolean): BottomSheetDialogBuilder {
        mDialog.setCancelable(cancelable)
        return this
    }

    fun setCanceledOnTouchOutside(cancelable: Boolean): BottomSheetDialogBuilder {
        mDialog.setCanceledOnTouchOutside(cancelable)
        return this
    }

    fun setOnCancelListener(cancelListener: DialogInterface.OnCancelListener?): BottomSheetDialogBuilder {
        mDialog.setOnCancelListener(cancelListener)
        return this
    }

    fun setItems(@MenuRes menuRes:Int, listener: AdapterView.OnItemClickListener?): BottomSheetDialogBuilder {
        val dummyMenu = PopupMenu(mDialog.context, null)
        val menu = dummyMenu.menu
        MenuInflater(mDialog.context).inflate(menuRes, menu)

        val items = mutableListOf<String>()
        val icons = mutableListOf<Drawable?>()
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.isEnabled) {
                items.add(item.title.toString())
                icons.add(item.icon)
            }
        }
        return setItems(items.toTypedArray(), icons.toTypedArray(), listener)
    }

    fun setItems(@StringRes resId:Array<Int>, @DrawableRes drawableResId:Array<Int>, listener: AdapterView.OnItemClickListener?): BottomSheetDialogBuilder
    {
        val items = arrayOf<String>()
        val icons = arrayOf<Drawable?>()
        for (i in 0 until resId.size) {
            items[i] = mDialog.context.getString(resId[i]);
            icons[i] = AppCompatResources.getDrawable(mDialog.context, resId[i]);
        }
        return setItems(items, icons, listener)
    }

    fun setItems(items:Array<String>, drawables:Array<Drawable?>, listener: AdapterView.OnItemClickListener?): BottomSheetDialogBuilder
    {
        mItems = items
        val arrayAdapter:ArrayAdapter<String> = object : ArrayAdapter<String>(
                mDialog.context,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = (super.getView(position, convertView, parent) as TextView)
                //val d = AppCompatResources.getDrawable(context, resId[position])
                drawables[position]?.apply {
                    context.resources.getDimensionPixelSize(R.dimen.navigation_icon_bounds).let {
                        setBounds(0, 0, it, it)
                        view.compoundDrawablePadding = it
                    }
                    view.setCompoundDrawables(this, null, null, null)
                }
                return view
            }
        }

        mDialog.findViewById<ListView>(android.R.id.list)?.let {
            it.adapter = arrayAdapter
            it.onItemClickListener = listener
        }
        return this
    }

    fun setSelectedItem(item: String): BottomSheetDialogBuilder {
        return this
    }

    fun create(): BottomSheetDialog = mDialog

    fun show(): BottomSheetDialog = mDialog.apply {
        this.show()
    }

    fun dismiss(): BottomSheetDialog = mDialog.apply {
        this.dismiss()
    }
}