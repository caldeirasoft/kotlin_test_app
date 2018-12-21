package com.caldeirasoft.basicapp.ui.discover

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork
import com.caldeirasoft.basicapp.ui.adapters.imageUrl

class DiscoverInfiniteAdapter(context: Context)
    : LoopingPagerAdapter<PodcastArtwork>(context, ArrayList(), true)
{
    override fun inflateView(viewType: Int, container: ViewGroup?, listPosition: Int): View {
        val inflater = LayoutInflater.from(this.context)
        return inflater.inflate(R.layout.fragment_discover_slider, container, false)
    }

    override fun bindView(convertView: View?, listPosition: Int, viewType: Int) {
        convertView?.findViewById<ImageView>(R.id.imageview_background)?.let {
            it.imageUrl(this.getItem(listPosition).artworkUrl)
        }

    }
}