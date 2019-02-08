package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.databinding.FragmentEpisodeBinding
import com.caldeirasoft.basicapp.presentation.utils.extensions.lazyArg
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.utils.extensions.bindView
import com.caldeirasoft.basicapp.presentation.utils.extensions.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EpisodeInfoFragment2 :
        BindingFragment<FragmentEpisodeBinding>()
{
    // private fields
    private var listener: OnFragmentInteractionListener? = null

    // arguments
    private val args by lazy { EpisodeInfoFragment2Args.fromBundle(arguments!!) }

    // viewmodel
    private val mViewModel : EpisodeInfoViewModel by viewModel { parametersOf(args.episode) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodeBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel

            // set transition item
            val imageView:ImageView = it.root.findViewById(R.id.imageview_thumbnail)
            ViewCompat.setTransitionName(imageView, args.transitionName)

            return it.root
        }
    }

    override fun onCreate() {
        initAppBar()
        initFab()
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

    private fun initAppBar() {
        mBinding.bottomAppBarEpisodeInfo
                .inflateMenu(R.menu.bottomappbar_episode_archive_menu)
    }

    private fun initFab() {
        /*fab = rootView.findViewById(R.id.fragment_fab)
        fab.setOnClickListener {
            Snackbar.make(it, "Snackbar", Snackbar.LENGTH_SHORT).show()
        }*/
    }

    private fun initObservers() {
        mViewModel.sectionData.observeK(this) {
            mBinding.bottomAppBarEpisodeInfo
                    .apply {
                       /* replaceMenu(
                                when (it) {
                                    SectionState.INBOX.value -> R.menu.bottomappbar_episode_inbox_menu
                                    SectionState.QUEUE.value -> R.menu.bottomappbar_episode_queue_menu
                                    else -> R.menu.bottomappbar_episode_archive_menu
                                })*/
                        fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                    }
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
}