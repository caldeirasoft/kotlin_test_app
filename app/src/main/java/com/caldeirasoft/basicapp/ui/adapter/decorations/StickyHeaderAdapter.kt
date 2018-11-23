package com.caldeirasoft.basicapp.ui.adapter.decorations

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


/**
 * The adapter to assist the [StickyHeaderDecorator] in creating and binding the header views.
 * @param <T> the header view holder
</T> */
interface StickyHeaderAdapter<T : RecyclerView.ViewHolder> {

    /**
     * Returns the header id for the item at the given position.
     * @param position the item position
     * *
     * @return the header id
     */
    fun getHeaderId(position: Int): Long

    /**
     * Creates a new header ViewHolder.
     * @param parent the header's view parent
     * *
     * @return a view holder for the created view
     */
    fun onCreateHeaderViewHolder(parent: ViewGroup): T

    /**
     * Updates the header view to reflect the header data for the given position
     * @param viewholder the header view holder
     * *
     * @param position the header's item position
     */
    fun onBindHeaderViewHolder(viewholder: T, position: Int)
}