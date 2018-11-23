package com.caldeirasoft.basicapp.ui.base

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.EnumPodcastLayout
import com.caldeirasoft.basicapp.data.preferences.UserPref
import com.caldeirasoft.basicapp.data.repository.PodcastRepository
import kotlinx.android.synthetic.main.fragment_podcasts.view.*
import java.util.concurrent.Executor
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.data.entity.Episode
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.doAsyncResult
import java.net.URL
import java.util.*
import kotlin.math.max


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