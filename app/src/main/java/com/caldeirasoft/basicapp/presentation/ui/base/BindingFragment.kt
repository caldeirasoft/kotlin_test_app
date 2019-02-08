package com.caldeirasoft.basicapp.presentation.ui.base

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.R

abstract class BindingFragment<B : ViewDataBinding> : Fragment()
{
    protected lateinit var mBinding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.image_transition)
    }

    abstract fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View?

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return this.onCreateView(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreate()
    }

    protected open fun onCreate() {}

}