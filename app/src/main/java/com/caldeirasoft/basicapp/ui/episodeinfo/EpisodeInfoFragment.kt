package com.caldeirasoft.basicapp.ui.episodeinfo

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.enum.SubscribeAction
import com.caldeirasoft.basicapp.databinding.FragmentEpisodeBinding
import com.caldeirasoft.basicapp.databinding.FragmentPodcastinfoBinding
import com.caldeirasoft.basicapp.extensions.lazyArg
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.ui.common.MainNavigationFragment
import com.caldeirasoft.basicapp.ui.extensions.observeK
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders
import com.caldeirasoft.basicapp.ui.podcastdetail.filter.PodcastFilterFragment
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_EXPANDED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_podcastinfo.*

class EpisodeInfoFragment : BindingFragment<FragmentEpisodeBinding>() {

    val episode by lazyArg<Episode>(EXTRA_FEED_ID)
    private var listener: OnFragmentInteractionListener? = null

    private var menu: Menu? = null
    private val mViewModel by lazy { viewModelProviders<EpisodeInfoViewModel>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodeBinding.inflate(inflater, container, false)
                .apply {
                    setLifecycleOwner(this@EpisodeInfoFragment)
                    viewModel = mViewModel
                }
        return mBinding.root
    }

    override fun onCreate() {
        initObservers()
        initUi()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener?.onFragmentInteraction(Uri.parse("showBottomNavigationView=1"))
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun initObservers() {
        // set loading
        mViewModel.setEpisode(episode)
    }

    private fun initUi() {
        listener?.onFragmentInteraction(Uri.parse("hideBottomNavigationView=1"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}