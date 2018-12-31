package com.caldeirasoft.basicapp.ui.extensions

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

inline fun AppBarLayout.collapsedTitle(collapsingToolbarLayout: CollapsingToolbarLayout) {
    var isShow = false
    var scrollRange = -1
    addOnOffsetChangedListener( AppBarLayout.OnOffsetChangedListener{
        appbarlayout,verticaloffset ->
        if (scrollRange == -1) {
            scrollRange = this@collapsedTitle.getTotalScrollRange();
        }
        if (scrollRange + verticaloffset == 0) {
            //setTitle("Lionel Messi Masuk Gunadarma");
            collapsingToolbarLayout.isTitleEnabled = true
            isShow = true;
        } else if (isShow) {
            //setTitle("");
            collapsingToolbarLayout.isTitleEnabled = false
            isShow = false;
        }

    })
}

fun AppBarLayout.addFadingToolbar(collapsingToolbarLayout: CollapsingToolbarLayout) {
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener {
        appbarlayout,verticaloffset ->
        val percentage = Math.abs(verticaloffset).toFloat() / totalScrollRange
        collapsingToolbarLayout.alpha = 1 - percentage
    })
}