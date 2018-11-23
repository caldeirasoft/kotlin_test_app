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
 * A sticky header decoration for android's RecyclerView.
 */
class StickyHeaderDecoration <VH : RecyclerView.ViewHolder>
/**
 * @param mAdapter
 * the sticky header adapter to use
 */
@JvmOverloads constructor(private val mAdapter: StickyHeaderAdapter<VH>) : RecyclerView.ItemDecoration() {
    //最后调用 绘制顶部固定的header
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.childCount

        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)

            val adapterPos = parent.getChildAdapterPosition(child)
            //只有在最上面一个item或者有header的item才绘制ItemDecoration
            if (adapterPos != RecyclerView.NO_POSITION && (layoutPos == 0 || hasHeader(adapterPos))) {
                val header = getHeader(parent, adapterPos).itemView
                c.save()
                val left = child.left
                val top = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                c.translate(left.toFloat(), top.toFloat())
                header.translationX = left.toFloat()
                header.translationY = top.toFloat()
                header.draw(c)
                c.restore()
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        //得到该item所在的位置
        val position = parent.getChildAdapterPosition(view)

        var headerHeight = 0
        //在使用adapterPosition时最好的加上这个判断
        if (position != RecyclerView.NO_POSITION && hasHeader(position)) {
            //获取到ItemDecoration所需要的高度
            val header = getHeader(parent, position).itemView
            headerHeight = header.height
        }
        outRect.set(0, headerHeight, 0, 0)
    }

    /**
     * 判断是否有header
     *
     * @param position
     * @return
     */
    private fun hasHeader(position: Int): Boolean {
        if (position == 0) {//第一个位置必然有
            return true
        }
        //判断和上一个的id不同则有header
        val previous = position - 1
        return mAdapter.getHeaderId(position) != mAdapter.getHeaderId(previous)
    }

    /**
     * 获得自定义的Header
     *
     * @param parent
     * @param position
     * @return
     */
    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        //创建HeaderViewHolder
        val holder = mAdapter.onCreateHeaderViewHolder(parent)
        val header = holder.itemView
        //绑定数据
        mAdapter.onBindHeaderViewHolder(holder, position)
        //测量View并且layout
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        //根据父View的MeasureSpec和子view自身的LayoutParams以及padding来获取子View的MeasureSpec
        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.paddingLeft + parent.paddingRight, header.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.paddingTop + parent.paddingBottom, header.layoutParams.height)
        //进行测量
        header.measure(childWidth, childHeight)
        //根据测量后的宽高放置位置
        header.layout(0, 0, header.measuredWidth, header.measuredHeight)
        return holder
    }

    /**
     * 计算距离顶部的高度
     *
     * @param parent
     * @param child
     * @param header
     * @param adapterPos
     * @param layoutPos
     * @return
     */
    private fun getHeaderTop(parent: RecyclerView, child: View, header: View, adapterPos: Int, layoutPos: Int): Int {
        val headerHeight = header.height
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {//处理最上面两个ItemDecoration切换时
            val count = parent.childCount
            val currentId = mAdapter.getHeaderId(adapterPos)
            for (i in 1 until count) {
                val adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId = mAdapter.getHeaderId(adapterPosHere)
                    //找到下一个不同类的view
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        //这里计算offset画个图会很清楚
                        val offset = next.y.toInt() - (headerHeight + getHeader(parent, adapterPosHere).itemView.height)
                        return if (offset < 0) {//如果大于0的话，此时并没有切换
                            offset
                        } else {
                            break
                        }
                    }
                }
            }
            //top不能小于0，否则最上面的ItemDecoration不会一直存在
            top = Math.max(0, top)
        }
        return top
    }

    companion object {
        const val NO_HEADER_ID = -1L
    }
}