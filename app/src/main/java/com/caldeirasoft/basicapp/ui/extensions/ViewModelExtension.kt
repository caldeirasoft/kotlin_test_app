package com.caldeirasoft.basicapp.ui.extensions

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders


inline fun <reified T : ViewModel> Fragment.viewModelProviders(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> FragmentActivity.viewModelProviders(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun FragmentManager.printActivityFragmentList() {
    this.fragments.let { it ->
        for(i in 0 until it.size) {
            it.get(i)?.tag?.let { tag ->
                Log.d("TAG_NAME_FRAGMENT", tag)
            }
        }
    }
}