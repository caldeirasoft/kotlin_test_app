package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.databinding.FragmentEpisodeBinding
import com.caldeirasoft.basicapp.databinding.FragmentEpisodedetailBinding
import com.caldeirasoft.basicapp.presentation.utils.extensions.lazyArg
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.utils.extensions.bindView
import com.caldeirasoft.basicapp.presentation.utils.extensions.findNavController
import com.caldeirasoft.basicapp.presentation.utils.widget.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EpisodeInfoDialogFragment :
        RoundedBottomSheetDialogFragment()
{
    // arguments
    protected lateinit var mBinding: FragmentEpisodedetailBinding
    private val mEpisode:Episode by lazyArg("episode")

    // viewmodel
    private val mViewModel : EpisodeInfoViewModel by viewModel { parametersOf(mEpisode) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentEpisodedetailBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface?) {
                val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.coordinatorLayout_root)
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()

        initAppBar()
        initFab()
        initObservers()
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