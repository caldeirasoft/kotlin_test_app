package com.caldeirasoft.basicapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.viewholder.BindingViewHolder

abstract class DataBindingAdapter<T, B : ViewDataBinding>(
        @LayoutRes val layout: Int,
        val itemDiffCallback: DiffUtil.ItemCallback<T>,
        val lifecycleOwner: LifecycleOwner?,
        protected val itemViewClickListener: ItemViewClickListener<T>? = null,
        protected vararg val clickAwareViewIds: Int = intArrayOf()
) : ListAdapter<T, BindingViewHolder<B>>(itemDiffCallback), IItemDataBindingAdapter<T>
{
    override fun onBindViewHolder(viewHolder: BindingViewHolder<B>, position: Int) {
        bindData(viewHolder.binding, position, getItem(position))
    }

    override fun getItem(position: Int): T? {
        return super.getItem(position)
    }


    abstract fun bindData(binding: B, position: Int, item: T?)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> =
        if (itemViewClickListener != null)
            ClickAwareViewHolder(layoutRes = layout, parent = parent, positionClick = this::onPositionClick, lifecycleOwner = lifecycleOwner)
        else
            BindingViewHolder(layoutRes = layout, parent = parent, lifecycleOwner = lifecycleOwner)


    protected open fun onPositionClick(position: Int, @IdRes viewId: Int) {
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }

}