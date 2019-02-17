package com.caldeirasoft.basicapp.presentation.ui.queue

import android.view.*
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class QueueFragment : EpisodeListFragment() {

    override val mViewModel: QueueViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.title = context?.getString(R.string.queue)
            return it.root
        }
    }
}