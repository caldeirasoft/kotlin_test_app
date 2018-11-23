package com.caldeirasoft.basicapp.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.caldeirasoft.basicapp.ui.base.BaseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import java.util.*

fun <T : ViewDataBinding> BaseActivity.setContentBinding(layoutId: Int): T {
    return DataBindingUtil.setContentView(this, layoutId)
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("fabVisibility")
fun fabVisibility(fab: FloatingActionButton, visible: Boolean) {
    if (visible) fab.show() else fab.hide()
}

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

@BindingAdapter("imageUrl")
fun ImageView.imageUrl(url: String?) {
    Picasso.with(this.context).load(url).into(this)
}

@BindingAdapter("imageUrl", "error")
fun ImageView.imageUrl(url: String?, error: Drawable) {
    Picasso.with(this.context).load(url).error(error).into(this)
}

@BindingAdapter("android:src")
fun ImageView.imageDrawable(drawable: Drawable) {
    this.setImageDrawable(drawable)
}

@BindingAdapter("android:src")
fun ImageView.imageDrawable(resId: Int) {
    this.setImageResource(resId)
}

@BindingAdapter("android:src")
fun FloatingActionButton.imageDrawable(drawable: Drawable) {
    this.setImageDrawable(drawable)
}

@BindingAdapter("android:src")
fun ImageButton.imageDrawable(drawable: Drawable) {
    this.setImageDrawable(drawable)
}

@BindingAdapter("android:drawableTint")
fun TextView.setDrawableTint(color: Int)
{
    var i:Int = 0
    val compounds = this.compoundDrawables
    val wrappedCompounds = ArrayList<Drawable>(compounds.size)
    for(i in 0..compounds.size)
    {
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