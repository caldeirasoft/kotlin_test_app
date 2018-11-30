package com.caldeirasoft.basicapp.ui.library

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.favorite.FavoriteFragment
import com.caldeirasoft.basicapp.ui.history.HistoryFragment
import com.caldeirasoft.basicapp.ui.podcast.PodcastFragment

class LibraryFragmentPagerAdapter(val context:Context, fm:FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> PodcastFragment()
            1 -> FavoriteFragment()
            2 -> HistoryFragment()
            else -> PodcastFragment()
        }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? =
        when (position) {
            1 -> context.resources.getString(R.string.starred)
            2 -> context.resources.getString(R.string.history)
            else -> context.resources.getString(R.string.podcasts)
        }
}
