package com.caldeirasoft.basicapp.presentation.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.FragmentLayout
import com.caldeirasoft.basicapp.presentation.bindingadapter.isVisible
import com.caldeirasoft.basicapp.presentation.ui.base.MediaPlayerBaseActivity
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoFragment
import kotlinx.android.synthetic.main.activity_main.*

@FragmentLayout(layoutId = R.layout.activity_main)
class MainActivity : MediaPlayerBaseActivity(),
        EpisodeInfoFragment.OnFragmentInteractionListener
{
    //private val mFirestore: FirebaseFirestore? = null
    //private static final int RC_SIGN_IN = 9001
    lateinit var navController: NavController

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
        val lastPathSegment = uri.lastPathSegment
        if (lastPathSegment == "hideBottomNavigationView=1") {
            navigation.isVisible = !navigation.isVisible
        }
        else if (lastPathSegment == "showBottomNavigationView=1") {
            navigation.isVisible = true
        }
    }
}
