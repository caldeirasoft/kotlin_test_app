package com.caldeirasoft.basicapp.presentation.ui.base.annotation

import androidx.annotation.LayoutRes

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentLayout(@LayoutRes val layoutId: Int)