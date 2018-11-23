package com.caldeirasoft.basicapp.ui.adapter.decorations

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App.Companion.context
import com.caldeirasoft.basicapp.R


class ItemOffsetDecoration : RecyclerView.ItemDecoration {

    private var mHorizontalOffset:Int = 0
    private var mVerticalOffset:Int = 0

    constructor(itemOffset: Int) {
        mHorizontalOffset = itemOffset
        mVerticalOffset = itemOffset
    }

    constructor(horizontalOffset: Int, verticalOffset: Int) {
        mHorizontalOffset = horizontalOffset
        mVerticalOffset = verticalOffset
    }

    constructor(context: Context, @DimenRes itemOffsetId: Int)
            : this(context.resources.getDimensionPixelSize(itemOffsetId))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mHorizontalOffset, mVerticalOffset, mHorizontalOffset, mVerticalOffset)
    }
}