package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.databinding.FragmentEpisodeBinding
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.presentation.extensions.lazyArg
import com.caldeirasoft.basicapp.presentation.extensions.observeK
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EpisodeInfoFragment : BindingFragment<FragmentEpisodeBinding>() {

    val episode by lazyArg<Episode>(EXTRA_FEED_ID)
    private var listener: OnFragmentInteractionListener? = null
    private val mViewModel : EpisodeInfoViewModel by viewModel { parametersOf(episode) }

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
        mViewModel.sectionData.observeK(this) {
            mBinding.bottomAppBarEpisodeInfo.inflateMenu(
                    when (it) {
                        SectionState.INBOX.value -> R.menu.bottomappbar_episode_inbox_menu
                        SectionState.QUEUE.value -> R.menu.bottomappbar_episode_queue_menu
                        else -> R.menu.bottomappbar_episode_archive_menu
                    }
            )
        }
    }

    private fun initUi() {
        listener?.onFragmentInteraction(Uri.parse("hideBottomNavigationView=1"))
    }

    private fun initMenu() {
        mBinding.bottomAppBarEpisodeInfo.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.app_bar_queue_next -> { }
                R.id.app_bar_queue_end -> { }
                R.id.app_bar_archive -> { }
                R.id.app_bar_favorite -> { }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
        const val COLLAPSED = "COLLAPSED"
    }
}