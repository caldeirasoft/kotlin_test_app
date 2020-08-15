package com.caldeirasoft.basicapp.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.ui.base.BaseActivity
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.FragmentLayout
import com.caldeirasoft.basicapp.presentation.utils.extensions.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    //private val mFirestore: FirebaseFirestore? = null
    //private static final int RC_SIGN_IN = 9001
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarTransparent()
        if (savedInstanceState == null)
            setupBottomNavigationBar()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)

        // register navigation scheme
        val navStartDestinationIds = listOf(R.id.inbox_dest, R.id.podcasts_dest, R.id.discover_dest)

        // setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
                navGraphId = R.navigation.navigation_graph,
                navStartDestinationIds = navStartDestinationIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.nav_host_container,
                intent = intent)

        currentNavController = controller
        // whenever the selected controller changed, setup the destination changed listener
        /*runBlocking {
            controller.collect { navController ->
                currentNavController = navController
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    TransitionManager.beginDelayedTransition(
                            contentContainer,
                            BottomNavigationViewTransition
                    )
                    val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
                    bottomNavigationView.isVisible = isTopLevelDestination
                }
            }
        }*/
    }

    override fun onSupportNavigateUp(): Boolean =
            currentNavController?.value?.navigateUp() ?: false

    /**
     * Set status bar transparent and app full screen
     */
    private fun statusBarTransparent() {
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            //attributes.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
            statusBarColor = Color.TRANSPARENT
        }
    }
}
