package com.caldeirasoft.basicapp.ui.home

import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.ui.base.SingleLiveEvent
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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