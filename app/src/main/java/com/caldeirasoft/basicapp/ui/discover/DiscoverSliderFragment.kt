package com.caldeirasoft.basicapp.ui.discover

import android.os.Bundle
import android.view.*
import com.caldeirasoft.basicapp.data.repository.PodcastArtwork
import com.caldeirasoft.basicapp.databinding.FragmentDiscoverSliderBinding
import com.caldeirasoft.basicapp.ui.common.BindingFragment

class DiscoverSliderFragment : BindingFragment<FragmentDiscoverSliderBinding>() {
    private var podcastArtwork: PodcastArtwork? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            podcastArtwork = it.getParcelable(ARG_PODCAST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentDiscoverSliderBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@DiscoverSliderFragment)
            podcastArtwork = this@DiscoverSliderFragment.podcastArtwork
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {

        private const val ARG_PODCAST = "podcast"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Podcast.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(podcast1: PodcastArtwork) =
                DiscoverSliderFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PODCAST, podcast1)
                    }
                }
    }
}