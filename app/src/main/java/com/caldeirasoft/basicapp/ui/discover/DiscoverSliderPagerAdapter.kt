package com.caldeirasoft.basicapp.ui.discover

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork

class DiscoverSliderPagerAdapter(
        val context: Context,
        fm: FragmentManager
) : FragmentStatePagerAdapter(fm)
{
    private var podcasts : List<PodcastArtwork> = ArrayList()

    override fun getItem(position: Int): Fragment =
        DiscoverSliderFragment.newInstance(podcasts[position])

    override fun getCount(): Int = podcasts.size

    override fun getPageTitle(position: Int): CharSequence? =
            podcasts[position].podcast?.title


    fun submitPodcasts(podcastList: List<PodcastArtwork>) {
        podcasts = podcastList
        this.notifyDataSetChanged()
    }
}