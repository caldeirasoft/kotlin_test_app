package com.caldeirasoft.basicapp.ui.home

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private var mIsSigningIn: Boolean

    init {
        mIsSigningIn = false
    }

    fun getIsSigningIn(): Boolean = mIsSigningIn

    fun setIsSigningIn(mIsSigningIn: Boolean) {
        this.mIsSigningIn = mIsSigningIn
    }
}