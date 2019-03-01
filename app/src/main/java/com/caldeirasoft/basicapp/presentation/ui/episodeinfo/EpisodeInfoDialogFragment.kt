package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.view.*
import androidx.core.content.ContextCompat
import androidx.versionedparcelable.ParcelUtils
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.basicapp.databinding.FragmentEpisodedetailBinding
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.basicapp.presentation.utils.extensions.lazyArg
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.basicapp.presentation.utils.extensions.setSupportActionBar
import com.caldeirasoft.basicapp.presentation.utils.widget.RoundedBottomSheetDialogFragment
import com.caldeirasoft.castly.service.playback.const.Constants.Companion.EXTRA_DATE
import com.caldeirasoft.castly.service.playback.extensions.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EpisodeInfoDialogFragment :
        RoundedBottomSheetDialogFragment()
{
    // arguments
    private val mediaItem: MediaItem by lazyArg(EPISODE_ARG)

    // binding
    protected lateinit var mBinding: FragmentEpisodedetailBinding

    // viewmodel
    private val mViewModel : EpisodeInfoViewModel by viewModel { parametersOf(mediaItem) }

    // fab
    private var fab: FloatingActionButton? = null

    // override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentEpisodedetailBinding.inflate(inflater, container, false)
        mBinding.let {
            it.setLifecycleOwner(this)
            it.viewModel = mViewModel

            val bottomAppBar: BottomAppBar = it.root.findViewById(R.id.bottom_appbar_episodedetail)
            setSupportActionBar(bottomAppBar)

            fab = it.root.findViewById(R.id.fab_episode_info)

            return it.root
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface?) {
                val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()

        initFab()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bottomappbar_episode_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val archiveItem = menu.findItem(R.id.action_archive)
        val playNextItem = menu.findItem(R.id.action_play_next)
        val favoriteItem = menu.findItem(R.id.action_favorite)

        when (mViewModel.sectionData.value) {
            SectionState.ARCHIVE.value -> archiveItem.isVisible = false
            SectionState.QUEUE.value -> playNextItem.isVisible = false
        }

        if (mViewModel.isPlayingEpisode.value == true) {
            playNextItem.isVisible = false
        }

        mViewModel.episodeData.value?.let {
            favoriteItem.icon = ContextCompat.getDrawable(activity!!,
                    if (it.isFavorite) R.drawable.ic_favorite_border_24dp
                    else R.drawable.ic_favorite_24dp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId) {
            R.id.action_play_next -> { true }
            R.id.action_archive -> { true }
            R.id.action_favorite -> { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initFab() {
        /*fab = rootView.findViewById(R.id.fragment_fab)
        fab.setOnClickListener {
            Snackbar.make(it, "Snackbar", Snackbar.LENGTH_SHORT).show()
        }*/
    }

    private fun initObservers() {
        mViewModel.sectionData.observeK(this) {
            activity?.invalidateOptionsMenu()
        }

        mViewModel.isPlayingEpisode.observeK(this) {
            activity?.invalidateOptionsMenu()
        }

        mViewModel.mediaData.observeK(this) {
            it?.description?.metadata?.let {
                mBinding.title = it.title
                mBinding.albumArtUri = it.albumArtUri.toString()
                mBinding.podcastTitle = it.album
                mBinding.date = it.date
                mBinding.displayDescription = it.displayDescription
            }
        }
    }

    companion object {
        const val EPISODE_ARG = "episode"
    }
}