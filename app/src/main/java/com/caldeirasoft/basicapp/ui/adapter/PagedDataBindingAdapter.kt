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

    private var loadingState = LoadingState.OK

    override public fun getItem(position: Int): T? =
            if (position < itemCount - 1) super.getItem(position) else null

    override fun getItemViewType(position: Int): Int =
            if (position + 1 == itemCount) TYPE_FOOTER else TYPE_ITEM

    // itemCount + 1 for footer
    override fun getItemCount(): Int =
            super.getItemCount() + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> =
            when (viewType) {
                TYPE_FOOTER -> {
                    LayoutInflater.from(parent.context).let {
                        DataBindingUtil.inflate<B>(it, R.layout.footer_load_more, parent, false).let {
                            FooterViewHolder(it)
                        }
                    }
                    //FooterViewHolder(layoutRes = R.layout.footer_load_more, parent = parent)
                }
                else ->
                    if (itemViewClickListener != null)
                        ClickAwareViewHolder(layoutRes = layout, parent = parent, positionClick = this::onPositionClick, lifecycleOwner = lifecycleOwner)
                    else
                        BindingViewHolder(layoutRes = layout, parent = parent, lifecycleOwner = lifecycleOwner)
            }

    override fun onBindViewHolder(viewHolder: BindingViewHolder<B>, position: Int) {
        when (viewHolder) {
            is FooterViewHolder<B> ->
                (viewHolder as FooterViewHolder<B>).setLoadingState(loadingState)
            else ->
                bindData(viewHolder.binding, position, getItem(position))
        }
    }

    abstract fun bindData(binding: B, position: Int, item: T?)

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        itemViewClickListener?.onItemClick(getItem(position), position, viewId)
    }

    fun setState(state: LoadingState) {
        loadingState = state
        notifyItemChanged(super.getItemCount())
    }

    companion object {
        const val TYPE_ITEM = 1
        const val TYPE_FOOTER = 2
    }
}