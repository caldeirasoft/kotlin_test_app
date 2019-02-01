package com.caldeirasoft.basicapp.presentation.bindingadapter

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

@BindingAdapter("imageUrl")
fun ImageView.imageUrl(url: String?) {
    Picasso.with(this.context)
            .load(url)
            .placeholder(R.color.gray)
            .into(this)
}

@BindingAdapter("imageUrl", "error")
fun ImageView.imageUrl(url: String?, error: Drawable) {
    Picasso.with(this.context).load(url).error(error).into(this)
}

@BindingAdapter("blurUrl")
fun ImageView.blurUrl(url: String?) {
    Picasso.with(this.context)
            .load(url)
            .placeholder(R.color.gray)
            .transform(BlurTransformation(this.context))
            .into(this)
}

@BindingAdapter("blurUrl", "attachedScrimView")
fun ImageView.blurUrl(url: String?, attachScrimView: View?) {
    Picasso.with(this.context)
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


private fun setImageCallback(imageView: ImageView, scrimView: View?): Callback {
    return object : Callback {
        override fun onSuccess() {
            val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap
            Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                override fun onGenerated(palette: Palette?) {
                    val defaultColor = imageView.resources.getColor(R.color.black)
                    val vibrantColor = palette?.getDarkVibrantColor(defaultColor)?.let {
                        scrimView?.setBackgroundColor(it)
                    }
                }
            })
        }

        override fun onError() {
        }
    }
}
