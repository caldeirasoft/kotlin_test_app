package com.caldeirasoft.basicapp.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.caldeirasoft.basicapp.presentation.utils.extensions.setHeight

class StatusBarSizeView( context: Context,
                         attrs: AttributeSet
) : View(context, attrs) {
    companion object {
        // workaround: caching value because when changing page in bottom navigation view
        // setOnApplyWindowInsetsListener is not called
        @JvmStatic
        var viewHeight = -1
        const val DEFAULT_INSET = 96
    }

    init {
        if (!isInEditMode){
            //TODO: remove setBackgroundColor(context.colorSurface())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode){
            return
        }
        if (viewHeight == -1){
            val height = this.rootWindowInsets?.stableInsetTop ?: DEFAULT_INSET
            setHeight(height + 100)
        } else {
            setHeight(viewHeight)
        }
    }

}