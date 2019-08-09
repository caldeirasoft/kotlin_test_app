package com.caldeirasoft.basicapp.presentation.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.utils.extensions.getThemeColor
import com.google.android.material.button.MaterialButton
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton : MaterialButton {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.materialButtonStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var savedTextColor: ColorStateList = textColors
    private val spinnerDrawable: CircularProgressDrawable by lazy { loadProgressBarDrawable() }

    var isLoading: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue == newValue) return@observable

        if (newValue) {
            savedTextColor = textColors
            setTextColor(ColorStateList.valueOf(Color.TRANSPARENT))
            spinnerDrawable.start()
        } else {
            setTextColor(savedTextColor)
            spinnerDrawable.stop()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isLoading) {
            canvas.drawSpinner()
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who == spinnerDrawable
    }

    override fun onSaveInstanceState(): Parcelable {
        return LoadingButtonSavedState(super.onSaveInstanceState()).also { loadingState ->
            loadingState.isLoading = isLoading
            loadingState.isEnabled = isEnabled
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? LoadingButtonSavedState)?.let { loadingState ->
            isLoading = loadingState.isLoading
            isEnabled = loadingState.isEnabled
        }
        super.onRestoreInstanceState(state)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        spinnerDrawable.setColor(savedTextColor.getColorForState(drawableState, 0))
    }

    private fun Canvas.drawSpinner() {
        val size = min(width, height)
        val widthOffset = (width - size) / 2
        val heightOffset = (height - size) / 2
        spinnerDrawable.setBounds(widthOffset, heightOffset, width - widthOffset, height - heightOffset)
        spinnerDrawable.draw(this)
    }

    private fun loadProgressBarDrawable(): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            // setTint(context.getThemeColor(R.attr.colorPrimary))
            setStyle(CircularProgressDrawable.DEFAULT)
            setColor(context.getThemeColor(android.R.attr.textColorPrimaryInverse))
            callback = this@LoadingButton
        }
    }

    private fun CircularProgressDrawable.setColor(@ColorInt color: Int) {
        setColorSchemeColors(color and 0x00ffffff)
        alpha = Color.alpha(color)
    }

    private class LoadingButtonSavedState : BaseSavedState {
        constructor(superState: Parcelable?) : super(superState)
        constructor(source: Parcel) : super(source) {
            isLoading = source.readInt() == 1
            isEnabled = source.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(if (isLoading) 1 else 0)
            out.writeInt(if (isEnabled) 1 else 0)
        }

        var isLoading = false
        var isEnabled = true

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<LoadingButtonSavedState> {
                override fun createFromParcel(source: Parcel) = LoadingButtonSavedState(source)

                override fun newArray(size: Int) = arrayOfNulls<LoadingButtonSavedState?>(size)
            }
        }
    }
}