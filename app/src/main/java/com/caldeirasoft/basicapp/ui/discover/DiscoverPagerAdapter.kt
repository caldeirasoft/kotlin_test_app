package com.caldeirasoft.basicapp.ui.discover

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork
import com.caldeirasoft.basicapp.ui.common.CacheFragmentStatePagerAdapter

class DiscoverPagerAdapter(
        val context: Context,
        fm: FragmentManager
) : CacheFragmentStatePagerAdapter(fm)
{
    override fun createItem(position: Int): Fragment =
        when (position) {
            0 -> DiscoverForYouFragment.newInstance()
            1 -> DiscoverTopChartsFragment.newInstance()
            else -> DiscoverTopChartsFragment.newInstance()
        }

    override fun getCount() = 3

    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> "For you"
                1 -> "Top charts"
                else -> "Categories"
            }
}