package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun <T : ViewDataBinding> BaseActivity.setContentBinding(layoutId: Int): T {
    return DataBindingUtil.setContentView(this, layoutId)
}

@BindingAdapter("android:visibility")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("isVisible")
fun View.setIsVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("android:background")
fun View.setBackground(value: Drawable?) {
    value?.let { background = it }
}


@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("fabVisibility")
fun fabVisibility(fab: FloatingActionButton, visible: Boolean) {
    if (visible) fab.show() else fab.hide()
}

@BindingAdapter("android:clipToOutline")
fun setClipToOutline(view: View, boolean: Boolean) {
    view.clipToOutline = boolean
}

var View.isVisible
    get(): Boolean = if (this.visibility == View.VISIBLE) true else false
    set(value) = this.setVisibility(value)


