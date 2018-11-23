package com.caldeirasoft.basicapp.ui.adapter.decorations

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class HeaderViewDecoration(private val layout: Int) : RecyclerView.ItemDecoration() {

    private var mHeaderView: View? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            getHeader(parent)?.let {
                outRect.top = it.measuredHeight
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        mHeaderView?.let {
            it.layout(parent.left, 0, parent.right, it.measuredHeight)

            val childCount = parent.childCount
            if (childCount >= 0) {
                val child = parent.getChildAt(0)
                val adapterPos = parent.getChildAdapterPosition(child)

                if (adapterPos == RecyclerView.SCROLLBAR_POSITION_DEFAULT) {
                    c.save()
                    val height = mHeaderView?.measuredHeight ?: 0
                    val top = child.top - height
                    c.translate(0f, top.toFloat())
                    mHeaderView?.draw(c)
                    c.restore()
                }
            }
        }
    }

    /*
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        val header = inflater.inflate(layout, null, false)

        val childCount = parent.childCount
        if (childCount >= 0) {
            val child = parent.getChildAt(0)
            val adapterPos = parent.getChildAdapterPosition(child)

            if (adapterPos != RecyclerView.NO_POSITION) {
                canvas.save()

                val left = child.left.toFloat()
                val top = (child.y.toInt() - header.height).toFloat()
                canvas.translate(left, top)

                getHeader(parent).translationX = left
                getHeader(parent).translationY = top
                getHeader(parent).draw(canvas)

                canvas.restore()
            }
        }
    }
    */

    private fun getHeader(parent: RecyclerView): View? {
        if (mHeaderView == null) {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            mHeaderView = inflater.inflate(layout, null, false)
        }

        mHeaderView?.let {
            if (it.layoutParams == null) {
                it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            if (it.measuredHeight > 0 || it.measuredWidth > 0) {
                // header already measured
            } else {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View
                        .MeasureSpec.EXACTLY)
                val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredHeight, View
                        .MeasureSpec.UNSPECIFIED)

                val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                        parent.paddingLeft + parent.paddingRight, it.layoutParams
                        .width)
                val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                        parent.paddingTop + parent.paddingBottom, it.layoutParams
                        .height)

                it.measure(childWidth, childHeight)
                it.layout(0, 0, it.measuredWidth, it.measuredHeight)
            }
        }

        return mHeaderView
    }
}