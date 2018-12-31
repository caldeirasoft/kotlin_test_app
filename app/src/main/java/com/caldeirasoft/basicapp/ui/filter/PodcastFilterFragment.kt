package com.caldeirasoft.basicapp.ui.filter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailFilterBinding
import com.caldeirasoft.basicapp.ui.adapter.ChipAdapter
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailViewModel
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso

class PodcastFilterFragment() : BaseFilterFragment<Podcast, FragmentPodcastdetailFilterBinding>() {

    private val viewModel by lazy { viewModelProviders<PodcastDetailViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastdetailFilterBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@PodcastFilterFragment)
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = viewModel
    }

    override fun getSpanCount() = SPAN_NUMBER

    override fun getRecyclerViewId() = R.id.recyclerView_filter_section

    override fun getChipId(): Int  = R.id.chip_filter_section

    override fun getChipCloseId() : Int = R.id.chip_reset_filter


    override fun onBindItemViewHolder(item: Podcast?, holder: ChipAdapter.ViewHolder, drawable:Drawable?) {
        item?.let {
            holder.chipView.apply {
                chipText = it.title
                setupDrawableFromImageUrl(this, it.imageUrl, 48f)
            }
        }
    }

    private fun setupDrawableFromImageUrl(chipView: Chip, imageUrl: String?, floatImageUrlSize: Float?)
    {
        if (imageUrl.isNullOrEmpty())
            return

        val tag = chipView.tag
        val imageUrlSize = floatImageUrlSize?.let { Math.round(it)}
        val target: ChipPicassoTarget = when (tag) {
            null -> ChipPicassoTarget(chipView)
            is ChipPicassoTarget -> {
                Picasso.with(context).cancelRequest(tag)
                tag
            }
            else -> throw IllegalStateException("Chip should never use setTag(..)/getTag()")
        }

        Picasso.with(context).load(imageUrl).apply {
            imageUrlSize?.also { resize(it, it) }
            centerCrop()
            into(target)
        }
    }

    private class ChipPicassoTarget internal constructor(private val chipView: Chip) : com.squareup.picasso.Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            val drawable = BitmapDrawable(chipView.resources, bitmap)
            drawable.setBounds(0, 0, bitmap.width, bitmap.height)
            chipView.chipIcon = drawable
        }

        override fun onBitmapFailed(errorDrawable: Drawable) {}
        override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
    }

    private companion object {
        const val SPAN_NUMBER = 2
    }
}