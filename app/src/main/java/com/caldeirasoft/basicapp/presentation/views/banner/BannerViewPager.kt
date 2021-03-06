package com.caldeirasoft.basicapp.presentation.views.banner

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import java.util.*

class BannerViewPager(context: Context, attrs: AttributeSet) :
        ViewPager(context, attrs) {

    private var currentPage = 0
    private var imageCount = 0

    private var cornerRadius: Int = 0
    private var period: Long = 3000
    private var delay: Long = 3000
    private var autoCycle = true

    private var selectedDot = 0
    private var unselectedDot = 0
    private var errorImage = 0
    private var placeholder = 0

    init {

    }

    fun setImageList(imageList: List<String>){
        imageCount = imageList.size
        if(autoCycle){ autoSliding() }
    }

    private fun autoSliding(){
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == imageCount) {
                currentPage = 0
            }
            setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, delay, period)
    }

}