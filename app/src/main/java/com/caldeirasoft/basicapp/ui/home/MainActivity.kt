package com.caldeirasoft.basicapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.adapters.isVisible
import com.caldeirasoft.basicapp.ui.adapters.setIsVisible
import com.caldeirasoft.basicapp.ui.common.MediaPlayerBaseActivity
import com.caldeirasoft.basicapp.ui.discover.DiscoverFragment
import com.caldeirasoft.basicapp.ui.episodeinfo.EpisodeInfoFragment
import com.caldeirasoft.basicapp.ui.extensions.printActivityFragmentList
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.inbox.InboxFragment
import com.caldeirasoft.basicapp.ui.podcast.PodcastFragment
import com.caldeirasoft.basicapp.ui.queue.QueueFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MediaPlayerBaseActivity(),
        EpisodeInfoFragment.OnFragmentInteractionListener
{
    //private val mFirestore: FirebaseFirestore? = null
    //private static final int RC_SIGN_IN = 9001
    lateinit var navController: NavController
    private val viewModel by lazy { viewModelProviders<MainActivityViewModel>() }

    override fun getLayout(): Int = R.layout.activity_main

    private fun initializeBottomNav(navController: NavController) {
        NavigationUI.setupWithNavController(navigation, navController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHost: NavHostFragment = nav_host_fragment as NavHostFragment? ?: return
        navController = navHost.navController

        initializeBottomNav(navController)
    }


    override fun onBackPressed() {
        val currentDestination = NavHostFragment.findNavController(nav_host_fragment).currentDestination
        when (currentDestination?.id) {
            R.id.queueFragment -> {
                finish()
                return
            }
        }
        super.onBackPressed()
    }

    override fun onFragmentInteraction(uri: Uri) {
        var lastPathSegment = uri.lastPathSegment
        if (lastPathSegment == "hideBottomNavigationView=1") {
            navigation.isVisible = !navigation.isVisible
        }
        else if (lastPathSegment == "showBottomNavigationView=1") {
            navigation.isVisible = true
        }
    }
}
