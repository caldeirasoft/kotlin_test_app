package com.caldeirasoft.basicapp.presentation.ui.ext.banner

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso

@SuppressLint("WrongConstant")
class BannerPagerAdapter(var imageList: List<String>, private var errorImage: Int, private var placeholder: Int) : PagerAdapter() {

    private var itemClickListener: ItemClickListener? = null

    fun getItemUrl(position: Int): String? =
            imageList[position]

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
            (view == `object`)

    //destory view
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    //inflate the view and change its values
    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageView = ImageView(view.context)
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        getItemUrl(position)?.let { imageUrl ->
            Picasso.get()
                    .load(imageUrl!!) // String
                    .fit()
                    //.placeholder(placeholder)
                    //.error(errorImage)
                    .into(imageView)
        }

        view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.setOnClickListener { itemClickListener?.onItemSelected(position) }
        return imageView
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int = imageList.size

}