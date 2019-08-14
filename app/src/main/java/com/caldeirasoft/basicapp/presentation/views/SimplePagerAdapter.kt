package com.caldeirasoft.basicapp.presentation.views

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

@SuppressLint("WrongConstant")
class SimplePagerAdapter(var activity: FragmentActivity, var pager: ViewPager, var pageTitles: Array<String>) : PagerAdapter() {

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by [.instantiateItem]. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view Page View to check for association with `object`
     * @param obj Object to check for association with `view`
     * @return true if `view` is associated with the key object `object`
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)

    //destory view
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        //container.removeView(obj as View)
    }

    //inflate the view and change its values
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return pager.getChildAt(position)
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int = pageTitles.size

    override fun getPageTitle(position: Int) = pageTitles[position]

}