package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.caldeirasoft.basicapp.R
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import java.util.*



@BindingAdapter(value = ["title", "description"], requireAll = false)
fun setTitleDescription(view: TextView, title: String?, description: String?) {
    view.text = SpannableString("${title ?: ""}\n${description ?: ""}").apply {
        setSpan(
                TextAppearanceSpan(view.context, R.style.TextAppearance_MaterialComponents_Body1),
                0,
                title?.length ?: 0,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        description?.let {
            setSpan(
                    TextAppearanceSpan(view.context, R.style.TextAppearance_MaterialComponents_Caption),
                    (title?.length ?: 0) + 2,
                    length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        //val lineHeightSpan = LineHeightSpan.Standard(10)
        //setSpan(FontFamilySpan(view.context.font(R.font.regular)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
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


@BindingAdapter("android:drawableTint")
fun TextView.setDrawableTint(color: Int) {
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
