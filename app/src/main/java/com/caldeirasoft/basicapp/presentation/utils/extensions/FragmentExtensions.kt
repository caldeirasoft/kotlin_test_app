package com.caldeirasoft.basicapp.presentation.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment

/**
 * Created: 17/08/2018
 * By: Sang
 * Description:
 */
fun Fragment.setSupportActionBar(toolBar: Toolbar) {
    with(activity as AppCompatActivity) {
        setSupportActionBar(toolBar)
    }
}

/**
 * Navigation extensions
 */
fun Fragment.findNavController(): NavController {
    return NavHostFragment.findNavController(this)
}

fun <T: NavDirections>Fragment.navigateTo(direction: T) {
    findNavController().navigate(direction)
}

fun <T: NavDirections>Fragment.navigateTo(direction: T, options:NavOptions) {
    findNavController().navigate(direction, options)
}

fun <T: NavDirections>Fragment.navigateTo(direction: T, extras: Navigator.Extras) {
    findNavController().navigate(direction, extras)
}

fun Fragment.postponeEnterTransition(delay: Long) {
    view?.let {
        postponeEnterTransition()
        it.postDelayed({
            startPostponedEnterTransition()
        }, delay)
    }
}