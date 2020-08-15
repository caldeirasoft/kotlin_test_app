package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.EpoxyRecyclerView
import com.caldeirasoft.basicapp.presentation.ui.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun <T : ViewDataBinding> BaseActivity.setContentBinding(layoutId: Int): T {
    return DataBindingUtil.setContentView(this, layoutId)
}

@BindingAdapter("android:visibility")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("isVisible")
fun View.setIsVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("android:background")
fun View.setBackground(value: Drawable?) {
    value?.let { background = it }
}

@BindingAdapter("roundedCorners")
fun setRoundedCorners(view: EpoxyRecyclerView, rounded: Boolean) {
    view.background = MaterialShapeDrawable().apply {
        this.shapeAppearanceModel = ShapeAppearanceModel().apply {
            when (rounded) {
                true -> setCornerSize(18f)
            }
        }
    }
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("fabVisibility")
fun fabVisibility(fab: FloatingActionButton, visible: Boolean) {
    if (visible) fab.show() else fab.hide()
}

@BindingAdapter("android:clipToOutline")
fun setClipToOutline(view: View, boolean: Boolean) {
    view.clipToOutline = boolean
}

@BindingAdapter(
        "paddingLeftSystemWindowInsets",
        "paddingTopSystemWindowInsets",
        "paddingRightSystemWindowInsets",
        "paddingBottomSystemWindowInsets",
        requireAll = false
)
fun View.applySystemWindowInsetsPadding(
        previousApplyLeft: Boolean,
        previousApplyTop: Boolean,
        previousApplyRight: Boolean,
        previousApplyBottom: Boolean,
        applyLeft: Boolean,
        applyTop: Boolean,
        applyRight: Boolean,
        applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
            previousApplyTop == applyTop &&
            previousApplyRight == applyRight &&
            previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, padding, _ ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.setPadding(
                padding.left + left,
                padding.top + top,
                padding.right + right,
                padding.bottom + bottom
        )
    }
}

@BindingAdapter(
        "marginLeftSystemWindowInsets",
        "marginTopSystemWindowInsets",
        "marginRightSystemWindowInsets",
        "marginBottomSystemWindowInsets",
        requireAll = false
)
fun View.applySystemWindowInsetsMargin(
        previousApplyLeft: Boolean,
        previousApplyTop: Boolean,
        previousApplyRight: Boolean,
        previousApplyBottom: Boolean,
        applyLeft: Boolean,
        applyTop: Boolean,
        applyRight: Boolean,
        applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
            previousApplyTop == applyTop &&
            previousApplyRight == applyRight &&
            previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, _, margin ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margin.left + left
            topMargin = margin.top + top
            rightMargin = margin.right + right
            bottomMargin = margin.bottom + bottom
        }
    }
}


var View.isVisible
    get(): Boolean = if (this.visibility == View.VISIBLE) true else false
    set(value) = this.setVisibility(value)

fun View.doOnApplyWindowInsets(block: (View, WindowInsets, InitialPadding, InitialMargin) -> Unit) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
        view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
            ?: throw IllegalArgumentException("Invalid view layout params")
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
