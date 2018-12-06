package com.caldeirasoft.basicapp.util

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import okhttp3.logging.HttpLoggingInterceptor
import ss.com.bannerslider.ImageLoadingService
import java.util.*
import java.util.logging.Logger

class PicassoImageLoadingService(var context:Context) : ImageLoadingService {
    override fun loadImage(url: String?, imageView: ImageView?) {
        Picasso.with(context).load(url).into(imageView)
    }

    override fun loadImage(resource: Int, imageView: ImageView?) {
        Picasso.with(context).load(resource).into(imageView)
    }

    override fun loadImage(url: String?, placeHolder: Int, errorDrawable: Int, imageView: ImageView?) {
        Picasso.with(context).load(url).placeholder(placeHolder).error(errorDrawable).into(imageView)
    }
}