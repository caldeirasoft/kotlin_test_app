package com.caldeirasoft.basicapp.presentation.ui.base.annotation

import androidx.annotation.MenuRes

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentMenu(val hasMenu: Boolean = false, @MenuRes val menuId: Int)