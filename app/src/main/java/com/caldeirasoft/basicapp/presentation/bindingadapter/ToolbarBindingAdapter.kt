package com.caldeirasoft.basicapp.presentation.bindingadapter

import android.graphics.drawable.Drawable
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["icon"])
fun Toolbar.toolbarBindIcon(drawable: Drawable) {
    this.navigationIcon = drawable
}

@BindingAdapter(value = ["subTitle"])
fun <S> Toolbar.toolbarBindSubTitle(subTitle: S?) where S : CharSequence {
    this.subtitle = subTitle
}

@BindingAdapter("menu")
fun Toolbar.toolbarBindMenu(@MenuRes menuRes: Int) {
    this.inflateMenu(menuRes)
}

@BindingAdapter(value = ["menu", "menuListener"], requireAll = true)
fun Toolbar.toolbarBindMenu(@MenuRes menuRes: Int, callback: Toolbar.OnMenuItemClickListener?) {
    this.inflateMenu(menuRes)
    if (callback != null) {
        this.setOnMenuItemClickListener(callback)
    }
}
