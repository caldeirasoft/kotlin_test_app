package com.caldeirasoft.basicapp.ui.adapters

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

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
