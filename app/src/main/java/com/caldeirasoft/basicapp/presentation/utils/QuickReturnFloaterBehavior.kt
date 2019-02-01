package com.caldeirasoft.basicapp.presentation.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL

class QuickReturnFloaterBehavior(
        val context: Context,
        val attrs: AttributeSet
)
    : CoordinatorLayout.Behavior<View>(context, attrs)
{
    private var distance:Int = 0

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && distance < 0 || dy < 0 && distance > 0) {
            child.animate().cancel()
            distance = 0
        }
        distance += dy;
        val height:Int = when (child.getHeight() > 0) {
            true -> child.height
            else -> 600
        }
        if (distance > height && child.isShown()) {
            hide(child);
        } else if (distance < 0 && !child.isShown()) {
            show(child);
        }
    }

    fun hide(view:View) {
        view.setVisibility(View.GONE);// use animate.translateY(height); instead
    }

    fun show(view:View) {
        view.setVisibility(View.VISIBLE);// use animate.translateY(-height); instead
    }
}