package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.palette.graphics.Palette
import com.caldeirasoft.basicapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import java.lang.Exception

@BindingAdapter("imageUrl")
fun ImageView.imageUrl(url: String?) {
    Picasso.get()
            .load(url)
            .placeholder(R.color.gray)
            .into(this)
}

@BindingAdapter("imageUrl", "error")
fun ImageView.imageUrl(url: String?, error: Drawable) {
    Picasso.get()
            .load(url)
            .error(error)
            .into(this)
}

@BindingAdapter("blurUrl")
fun ImageView.blurUrl(url: String?) {
    Picasso.get()
            .load(url)
            .placeholder(R.color.gray)
            .transform(BlurTransformation(this.context))
            .into(this)
}

@BindingAdapter("blurUrl", "attachedScrimView")
fun ImageView.blurUrl(url: String?, attachScrimView: View?) {
    Picasso.get()
            .load(url)
            .placeholder(R.color.gray)
            .transform(BlurTransformation(this.context))
            .into(this, setImageCallback(this, attachScrimView))
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

@BindingAdapter("app:animating")
fun ImageButton.animating(animating: Boolean) {
    val drawable = this.drawable
    if (drawable is Animatable) {
        if (animating)
            drawable.start()
        else drawable.stop()
    }
}


private fun setImageCallback(imageView: ImageView, scrimView: View?): Callback {
    return object : Callback {
        override fun onSuccess() {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            Palette.from(bitmap).generate { palette ->
                val defaultColor = imageView.resources.getColor(R.color.black)
                val vibrantColor = palette?.getDarkVibrantColor(defaultColor)?.let {
                    //scrimView?.setBackgroundColor(it)
                }
            }
        }

        override fun onError(e: Exception) {
        }
    }
}