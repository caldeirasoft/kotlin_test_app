package com.caldeirasoft.basicapp.ui.discover

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork
import com.caldeirasoft.basicapp.databinding.FragmentPodcastSliderBinding
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_podcast_slider.*

private const val ARG_PODCAST = "podcast"

class PodcastFragment : BindingFragment<FragmentPodcastSliderBinding>() {
    private var podcastArtwork: PodcastArtwork? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            podcastArtwork = it.getParcelable(ARG_PODCAST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentPodcastSliderBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@PodcastFragment)
            podcastArtwork = this@PodcastFragment.podcastArtwork
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    /*
    private fun setupThumbnail() {
        Picasso.with(this.requireContext())
                .load(podcastArtwork?.artworkUrl)
                .into(imageview_background, setupThumbnailCallback(imageview_background))
    }
    */

    /*
    private fun setupThumbnailCallback(imageView: ImageView): Callback {
        return object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap
                Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                    override fun onGenerated(palette: Palette?) {
                        val defaultColor = resources.getColor(R.color.black)
                        val vibrantColor = palette?.getDarkVibrantColor(defaultColor)?.let {

                            collapsing_toolbar.setBackgroundColor(it)
                            collapsing_toolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
                            collapsing_toolbar.setContentScrimColor(it)
                            collapsing_toolbar.setStatusBarScrimColor(it)
                        }

                        window.apply {
                            //clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                            //addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                            //statusBarColor = vibrantColor
                        }
                    }
                })
            }

            override fun onError() {
            }
        }
    }
    */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Podcast.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(podcast1: PodcastArtwork) =
                PodcastFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PODCAST, podcast1)
                    }
                }
    }
}