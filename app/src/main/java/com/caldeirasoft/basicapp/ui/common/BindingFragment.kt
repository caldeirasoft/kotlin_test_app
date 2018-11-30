package com.caldeirasoft.basicapp.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BindingFragment<B : ViewDataBinding> : Fragment()
{
    protected lateinit var mBinding: B

    abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return this.onCreateView(inflater, container)
    }
}