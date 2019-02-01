package com.caldeirasoft.basicapp.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.presentation.ui.base.annotation.AnnotationManager

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val meta = AnnotationManager.getLayoutOrThrow(this)
        val view = inflater.inflate(meta.layoutId, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreate()
    }

    abstract protected fun onCreate()
}