package com.caldeirasoft.basicapp.ui.common

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import org.jetbrains.anko.doAsyncResult
import java.net.URL


abstract class TabLayoutHelper()
{
    companion object {
        fun drawableFromUrl(view:View, url: String): Drawable? {
            return doAsyncResult {
                URL(url).openConnection()?.let {
                    it.connect()
                    it.getInputStream().let {
                        BitmapFactory.decodeStream(it).let {
                            BitmapDrawable(view.resources, it)
                        }
                    }
                }
            }.get()
        }
    }
}