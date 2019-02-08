package com.caldeirasoft.basicapp.presentation.ui.inbox

import android.view.*
import androidx.navigation.NavDirections
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.presentation.ui.episodelist.EpisodeListFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class InboxFragment : EpisodeListFragment() {

    override val mViewModel: InboxViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.title = context?.getString(R.string.inbox)
            return it.root
        }
    }

    override fun getEpisodeDirection(episode: Episode, transitionName: String): NavDirections =
        InboxFragmentDirections.goToEpisode(episode, transitionName)
}
