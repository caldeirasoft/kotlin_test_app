package com.caldeirasoft.basicapp.ui.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner


class SimplePagedDataBindingAdapter<T, B : ViewDataBinding>(
        @LayoutRes val layoutId: Int,
        private val variableId: Int,
        lifecycleOwner: LifecycleOwner,
        itemViewClickListener: ItemViewClickListener<T>,
        vararg clickAwareViewIds: Int = intArrayOf()
) : PagedDataBindingAdapter<T, B>(
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