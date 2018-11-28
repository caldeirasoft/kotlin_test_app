package com.caldeirasoft.basicapp.ui.home

import android.os.Bundle
import android.os.PersistableBundle
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.printActivityFragmentList
import com.caldeirasoft.basicapp.ui.base.MediaPlayerBaseActivity
import com.caldeirasoft.basicapp.ui.catalog.CatalogFragment
import com.caldeirasoft.basicapp.ui.inbox.InboxFragment
import com.caldeirasoft.basicapp.ui.library.LibraryFragment
import com.caldeirasoft.basicapp.ui.podcast.PodcastFragment
import com.caldeirasoft.basicapp.ui.queue.QueueFragment
import com.caldeirasoft.basicapp.viewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : MediaPlayerBaseActivity()
{
    //private val mFirestore: FirebaseFirestore? = null
    //private static final int RC_SIGN_IN = 9001
    private val viewModel by lazy { viewModelProviders<MainActivityViewModel>() }

    override fun getLayout(): Int = R.layout.activity_main

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_queue -> {
                super.addFragment(QueueFragment(), "queue", false)
                setTitle("queue")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_inbox -> {
                super.addFragment(InboxFragment(), "inbox", true)
                setTitle("inbox")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_podcasts -> {
                super.addFragment(PodcastFragment(), "podcasts", true)
                setTitle("podcasts")
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_catalog -> {
                super.addFragment(CatalogFragment(), "catalog", true)
                setTitle("catalog")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportFragmentManager?.printActivityFragmentList()

        if (savedInstanceState == null) {
            super.addFragment(QueueFragment(), "queue", false)
            setTitle("queue")
        }

        // Enable Firestore logging
        //FirebaseFirestore.setLoggingEnabled(true)

        // Initialize Firestore and the main RecyclerView
        //initFirestore()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        supportFragmentManager.apply {
            if (backStackEntryCount > 0) {
                getBackStackEntryAt(backStackEntryCount - 1).name?.let {
                    findFragmentByTag(it)?.apply {
                        addFragment(this, it, false)
                    }
                    popBackStackImmediate()
                }

                when (backStackEntryCount) {
                    0 -> this@MainActivity.navigation.menu.findItem(R.id.navigation_queue)?.setChecked(true)

                    else ->
                        getBackStackEntryAt(backStackEntryCount - 1).name?.let {
                            findFragmentByTag(it)?.let {
                                if (it is IMainFragment) {
                                    (it as IMainFragment).let {
                                        this@MainActivity.navigation.menu.findItem(it.getMenuItem())?.setChecked(true)
                                    }
                                }
                            }
                        }
                }
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    /*
    private fun initFirestore() {
        //TODO
    }

    private fun shouldStartSignIn(): Boolean =
        !viewModel.getIsSigningIn() && FirebaseAuth.getInstance().currentUser == null


    private fun startSignIn()
    {
        var providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()

        startActivityForResult(intent, 9001)
        viewModel.setIsSigningIn(true)
    }
    */
}
