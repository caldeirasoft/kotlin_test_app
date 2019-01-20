package com.caldeirasoft.basicapp.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.R

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(getLayout(), container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreate()
    }

    /**
     * Adds a [Fragment] to this activity's layout.
     */
    fun addFragment(fragment: Fragment, tag: String,
                    addToBackSTack: Boolean) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment, tag)
        if (addToBackSTack) fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    abstract protected fun getLayout(): Int
    abstract protected fun onCreate()
}