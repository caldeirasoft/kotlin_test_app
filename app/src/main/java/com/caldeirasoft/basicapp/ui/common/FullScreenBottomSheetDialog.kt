package com.caldeirasoft.basicapp.ui.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import android.util.DisplayMetrics
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class FullScreenBottomSheetDialog : BottomSheetDialogFragment()
{
    protected var viewDataBinding: ViewDataBinding? = null
    private var _behavior: BottomSheetBehavior<*>? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = View.inflate(this.context, getLayout(), null)

        view.minimumHeight = 0
        dialog!!.setContentView(view)

        viewDataBinding = DataBindingUtil.bind(view)

        _behavior = BottomSheetBehavior.from(view.parent as View)
        _behavior!!.skipCollapsed = true

        makeFullScreen(view)
        this.onCreate()
    }

    abstract protected fun getLayout(): Int
    abstract protected fun onCreate()

    private val bottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback()
    {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN)
                dismiss()
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun makeFullScreen(inflatedView: View)
    {
        val params = (inflatedView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(bottomSheetBehaviorCallback)
        }

        val parent = inflatedView.parent as View
        parent.fitsSystemWindows = true
        val bottomSheetBehavior = BottomSheetBehavior.from(parent)
        inflatedView.measure(0, 0)
        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val screenHeight = displaymetrics.heightPixels
        bottomSheetBehavior.peekHeight = (screenHeight * .7).toInt()
        inflatedView.minimumHeight = screenHeight

        if (params.behavior is BottomSheetBehavior<*>) {
            (params.behavior as BottomSheetBehavior<*>).setBottomSheetCallback(bottomSheetBehaviorCallback)
        }

        params.height = screenHeight
        parent.layoutParams = params
    }
}