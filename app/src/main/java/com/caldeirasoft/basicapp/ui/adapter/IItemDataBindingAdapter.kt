package com.caldeirasoft.basicapp.ui.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner


interface IItemDataBindingAdapter<T>
{
    fun getItem(position: Int): T?

    fun getItemCount(): Int
}