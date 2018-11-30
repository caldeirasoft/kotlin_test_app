package com.caldeirasoft.basicapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.viewholder.BindingViewHolder
import com.caldeirasoft.basicapp.ui.adapter.viewholder.FooterViewHolder
import com.caldeirasoft.basicapp.util.LoadingState


abstract class PagedDataBindingAdapter<T, B : ViewDataBinding>(
        @LayoutRes val layout: Int,
        val itemDiffCallback: DiffUtil.ItemCallback<T>,
        val lifecycleOwner: LifecycleOwner?,
        private val itemViewClickListener: ItemViewClickListener<T>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : PagedListAdapter<T, BindingViewHolder<B>>(itemDiffCallback), IItemDataBindingAdapter<T>
{

    override fun getItem(position: Int): T? = super.getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> =
            if (itemViewClickListener != null)
                ClickAwareViewHolder(layoutRes = layout, parent = parent, positionClick = this::onPositionClick, lifecycleOwner = lifecycleOwner)
            else
                BindingViewHolder(layoutRes = layout, parent = parent, lifecycleOwner = lifecycleOwner)

    override fun onBindViewHolder(viewHolder: BindingViewHolder<B>, position: Int) {
        bindData(viewHolder.binding, position, getItem(position))
    }

    abstract fun bindData(binding: B, position: Int, item: T?)

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }
}