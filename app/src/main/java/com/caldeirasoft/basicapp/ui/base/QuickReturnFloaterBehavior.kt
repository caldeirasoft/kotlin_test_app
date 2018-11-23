package com.caldeirasoft.basicapp.ui.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat.SCROLL_AXIS_VERTICAL
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import java.util.*
import java.util.jar.Attributes

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