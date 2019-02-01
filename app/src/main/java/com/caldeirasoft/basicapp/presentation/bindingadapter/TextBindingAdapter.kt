package com.caldeirasoft.basicapp.presentation.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import java.util.*

@BindingAdapter("textDayOfMonth")
fun TextView.textDayOfMonth(date: Long?) {
    date?.let {
        Instant.ofEpochMilli(it).let {
            it.atZone(ZoneOffset.UTC).toLocalDateTime().dayOfMonth.let {
                this.text = it.toString()
            }
        }
    }
}

@BindingAdapter("textMonth")
fun TextView.textMonth(date: Long?) {
    date?.let {
        Instant.ofEpochMilli(it).let {
            it.atZone(ZoneOffset.UTC).toLocalDateTime().month.let {
                this.text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
        }
    }
}


@BindingAdapter("android:drawableTint")
fun TextView.setDrawableTint(color: Int) {
    var i: Int = 0
    val compounds = this.compoundDrawables
    val wrappedCompounds = ArrayList<Drawable>(compounds.size)
    for (i in 0..compounds.size) {
        compounds[i]?.let {
            DrawableCompat.wrap(it).mutate().apply {
                DrawableCompat.setTint(this, color)
                wrappedCompounds[i] = this
            }
        }
    }
    this.setCompoundDrawables(wrappedCompounds[0], wrappedCompounds[1], wrappedCompounds[2], wrappedCompounds[3])
    this.invalidate()
}
