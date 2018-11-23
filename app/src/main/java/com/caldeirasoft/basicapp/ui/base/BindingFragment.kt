package com.caldeirasoft.basicapp.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailBinding

abstract class BindingFragment<B : ViewDataBinding> : Fragment()
{
    protected lateinit var mBinding: B

    abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return this.onCreateView(inflater, container)
    }
}