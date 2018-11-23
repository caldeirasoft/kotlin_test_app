package com.caldeirasoft.basicapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.viewholder.BindingViewHolder


class SimpleSelectedDataBindingAdapter<T, B : ViewDataBinding>(
        @LayoutRes layoutId: Int,
        variableId: Int,
        val selectedVariableId: Int,
        lifecycleOwner: LifecycleOwner,
        itemViewClickListener: ItemViewClickListener<T>,
        vararg clickAwareViewIds: Int = intArrayOf()
) : SimpleDataBindingAdapter<T, B>(
        layoutId = layoutId,
        variableId = variableId,
        lifecycleOwner = lifecycleOwner,
        itemViewClickListener = itemViewClickListener,
        clickAwareViewIds = *clickAwareViewIds
)
{
    protected var selectedIndex:Int = RecyclerView.NO_POSITION

    override fun bindData(binding: B, position: Int, item: T?) {
        binding.setVariable(variableId, item)
        binding.setVariable(selectedVariableId, (selectedIndex == position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B>
    {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding:B = DataBindingUtil.inflate(inflater, layout, parent, false)
        binding.setLifecycleOwner(lifecycleOwner)
        return if (itemViewClickListener != null)
            ClickAwareViewHolder(binding, this::onPositionClick, *clickAwareViewIds)
        else
            ClickAwareViewHolder(binding, this::onSelectClick)
    }

    protected override fun onPositionClick(position: Int, @IdRes viewId: Int) {
        super.onPositionClick(position, viewId)
        setSelection(position)
    }

    protected fun onSelectClick(position: Int, @IdRes viewId: Int) {
        setSelection(position)
    }

    fun setSelection(position: Int) {
        if (position == RecyclerView.NO_POSITION)
            return

        notifyItemChanged(selectedIndex)
        selectedIndex = position
        notifyItemChanged(selectedIndex)
    }

}