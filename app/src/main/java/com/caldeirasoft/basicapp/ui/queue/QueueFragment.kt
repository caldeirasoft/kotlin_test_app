package com.caldeirasoft.basicapp.ui.queue

import android.view.*
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.ui.episodelist.EpisodeListFragment
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.home.IMainFragment

class QueueFragment : EpisodeListFragment() {

    override val mViewModel by lazy { viewModelProviders<QueueViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.title = context?.getString(R.string.queue)
            return it.root
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}