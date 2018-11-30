package com.caldeirasoft.basicapp

import android.app.Activity
import androidx.fragment.app.Fragment
import com.caldeirasoft.basicapp.ui.common.BaseActivity

inline fun Activity.addFragment(fragment: Fragment, tag: String, addToBackSTack: Boolean) =
        (this as BaseActivity)?.addFragment(fragment, tag, addToBackSTack)
