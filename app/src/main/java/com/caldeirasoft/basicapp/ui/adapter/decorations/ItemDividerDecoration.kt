package com.caldeirasoft.basicapp.ui.adapter.decorations

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App.Companion.context
import com.caldeirasoft.basicapp.R


class ItemDividerDecoration(
        val context: Context,
        private val height:Int,
        private val position:Int = 0
) : RecyclerView.ItemDecoration() {

    private val divider : Drawable
    private val bounds = Rect()

    init {
        val a:TypedArray = context.obtainStyledAttributes(arrayOf(android.R.attr.listDivider).toIntArray())
        divider = a.getDrawable(0)
        a.recycle()
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        canvas.save()
        val left: Int = 0
        val right: Int = parent.width

        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            if (shouldDrawDivider(child, parent)) {
                child.layoutParams?.let {
                    (it as RecyclerView.LayoutParams)?.let {
                        val top = child.bottom + it.bottomMargin
                        val bottom = top + height
                        divider.setBounds(left, right, top, bottom)
                        divider.draw(canvas)
                    }
                }
            }
        }
        canvas.restore()

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (shouldDrawDivider(view, parent)) {
            outRect.top = this.height
        }
    }

    private fun shouldDrawDivider(view: View, parent: RecyclerView) =
            (parent.getChildAdapterPosition(view) == position)
}