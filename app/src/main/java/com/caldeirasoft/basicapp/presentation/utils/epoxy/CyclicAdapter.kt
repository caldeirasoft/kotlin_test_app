package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView

class CyclicAdapter<T : RecyclerView.ViewHolder>(
        val adapter: RecyclerView.Adapter<T>)
    : RecyclerView.Adapter<T>() {

    private val observerDelegate = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            adapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            adapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            //Unsupported
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            adapter.notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    init {
        super.setHasStableIds(adapter.hasStableIds())
        super.registerAdapterDataObserver(observerDelegate)
    }

    fun getActualItemCount(): Int =
            adapter.itemCount

    override fun getItemCount(): Int =
            Integer.MAX_VALUE

    private fun adjustedPosition(position: Int) =
            position % getActualItemCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T =
            adapter.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: T, position: Int) =
            adapter.onBindViewHolder(holder, adjustedPosition(position))

    override fun onBindViewHolder(holder: T, position: Int, payloads: MutableList<Any>) =
            adapter.onBindViewHolder(holder, adjustedPosition(position), payloads)

    override fun getItemViewType(position: Int): Int =
            adapter.getItemViewType(adjustedPosition(position))

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        adapter.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long =
            adapter.getItemId(adjustedPosition(position))

    override fun onViewRecycled(holder: T) {
        adapter.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: T): Boolean =
            adapter.onFailedToRecycleView(holder)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        adapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: T) {
        adapter.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: T) {
        adapter.onViewDetachedFromWindow(holder)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter.unregisterAdapterDataObserver(observer)
    }
}