package com.caldeirasoft.basicapp.ui.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner


open class SimpleDataBindingAdapter<T, B : ViewDataBinding>(
        @LayoutRes val layoutId: Int,
        protected val variableId: Int,
        lifecycleOwner: LifecycleOwner,
        itemViewClickListener: ItemViewClickListener<T>,
        vararg clickAwareViewIds: Int = intArrayOf()
) : DataBindingAdapter<T, B>(
        layout = layoutId,
        lifecycleOwner = lifecycleOwner,
        itemDiffCallback = defaultItemDiffCallback<T>(),
        itemViewClickListener = itemViewClickListener,
        clickAwareViewIds = *clickAwareViewIds
)
{
    override fun bindData(binding: B, position: Int, item: T?) {
        binding.setVariable(variableId, item)
    }
}