package com.caldeirasoft.basicapp.ui.adapter.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView


open class BindingViewHolder<out B: ViewDataBinding>(val binding: B)
        : RecyclerView.ViewHolder(binding.root) {
        constructor(@LayoutRes layoutRes: Int, parent: ViewGroup)
                : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false))

        constructor(@LayoutRes layoutRes: Int, parent: ViewGroup, lifecycleOwner: LifecycleOwner?)
                : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false)) {
                binding.setLifecycleOwner(lifecycleOwner)
        }
}
