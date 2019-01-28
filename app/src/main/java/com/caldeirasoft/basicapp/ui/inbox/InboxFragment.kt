package com.caldeirasoft.basicapp.ui.inbox

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.service.sync.SyncAdapterManager
import com.caldeirasoft.basicapp.ui.episodelist.EpisodeListFragment
import com.caldeirasoft.basicapp.ui.episodes.EpisodesBaseFragment
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.home.IMainFragment
import kotlinx.android.synthetic.main.fragment_inbox.*

class InboxFragment : EpisodeListFragment() {

    override val mViewModel by lazy { viewModelProviders<InboxViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.title = context?.getString(R.string.inbox)
            return it.root
        }
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}
