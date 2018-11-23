package com.caldeirasoft.basicapp.ui.base

import android.graphics.Rect
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.EnumPodcastLayout
import com.caldeirasoft.basicapp.data.preferences.UserPref
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import kotlinx.android.synthetic.main.fragment_podcasts.view.*
import java.util.concurrent.Executor
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max


class RecyclerHeaderHelper
{
    private var mRecyclerView: RecyclerView? = null
    private var mDecoration: HeaderItemDecoration? = null
    private var mScroller: HeaderScroller? = null

    /**
     * Attach the given view as a header to the given recycler view.
     *
     * @param recyclerView The recycler view which will get the header view.
     * @param headerView The view which will be the header of the recycler view.
     */
    fun attach(recyclerView: RecyclerView, layout: Int) {
        clear()

        val inflater:LayoutInflater = LayoutInflater.from(recyclerView.context)
        val headerView = inflater.inflate(layout, null, false)
        mRecyclerView = recyclerView
        mDecoration = HeaderItemDecoration(headerView)
        mScroller = HeaderScroller(headerView)

        recyclerView.addOnScrollListener(mScroller!!)
        recyclerView.addItemDecoration(mDecoration!!)
    }

    /**
     * This method can be useful if the data of the recycler view adapter changed e.g. if new items has been inserted
     * before the first item. In this case the item decoration has the be redrawn.
     *
     * @param recyclerView The {@link RecyclerView} to which this handler is attached to.
     */
    fun refreshItemDecoration() {
        mRecyclerView?.removeItemDecoration(mDecoration!!)
        mRecyclerView?.addItemDecoration(mDecoration!!)
    }

    private fun clear() {
        mScroller?.clear()
        mDecoration?.clear()

        mScroller = null
        mDecoration = null
    }

    class HeaderScroller(headerView: View) : RecyclerView.OnScrollListener() {
        private var mHeaderView: View? = headerView

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val firstChild = recyclerView.layoutManager?.findViewByPosition(0)
            val maxDy = (mHeaderView?.bottom ?: 0).toFloat()
            val dY = when (firstChild) {
                null -> -maxDy
                else -> Math.max(Math.min(0f, firstChild.top.toFloat() - maxDy), -maxDy)
            }

            if (mHeaderView?.translationY != dY)
                mHeaderView?.translationY = dY
        }

        fun clear()
        {
            mHeaderView = null
        }
    }

    class HeaderItemDecoration(headerView: View) : RecyclerView.ItemDecoration() {
        private var mHeaderView: View? = headerView

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.layoutManager?.findViewByPosition(0) == view)
            {
                outRect.top = getHeaderHeight()
            }
            else {
                outRect.top = 0
            }
        }

        fun getHeaderHeight(): Int {
            var height = mHeaderView?.layoutParams?.height ?: 0
            if (height <= 0) {
                mHeaderView?.measure(0, 0)
                height = mHeaderView?.measuredHeight!!
            }

            if (height <= 0)
            {
                height = mHeaderView?.height!!
            }

            return height
        }

        fun clear() {
            mHeaderView = null
        }
    }
}