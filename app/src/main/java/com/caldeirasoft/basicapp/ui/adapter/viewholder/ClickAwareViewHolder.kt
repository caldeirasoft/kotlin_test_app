package com.caldeirasoft.basicapp.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner


open class ClickAwareViewHolder<out B: ViewDataBinding>(
        binding: B,
        private val positionClick: (Int,Int) -> Unit,
        @IdRes vararg viewIds: Int = intArrayOf()
) : BindingViewHolder<B>(binding), View.OnClickListener {

    constructor(@LayoutRes layoutRes: Int, parent: ViewGroup, positionClick: (Int,Int) -> Unit)
            : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false), positionClick)

    constructor(@LayoutRes layoutRes: Int, parent: ViewGroup, lifecycleOwner: LifecycleOwner?, positionClick: (Int, Int) -> Unit)
            : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false), positionClick) {
        binding.setLifecycleOwner(lifecycleOwner)
    }

    init {
        if (viewIds.isEmpty())
            binding.root.setOnClickListener(this)
        else {
            val root = binding.root
            viewIds.forEach { root.findViewById<View>(it).setOnClickListener(this@ClickAwareViewHolder) }
        }
    }

    override fun onClick(v: View?) {
        positionClick.invoke(adapterPosition, v?.id ?: View.NO_ID)
    }
}