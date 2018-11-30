package com.caldeirasoft.basicapp.ui.episodedetail

import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.Observer
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.BR
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.ui.common.FullScreenBottomSheetDialog
import com.caldeirasoft.basicapp.viewModelProviders


class EpisodeDetailDialog() : FullScreenBottomSheetDialog() {
    lateinit var episode: Episode
    lateinit var podcast: Podcast
    private val viewModel by lazy { viewModelProviders<EpisodeDetailViewModel>() }

    override fun getLayout(): Int = R.layout.dialog_episodedetail

    override fun onCreate() {
        viewDataBinding?.apply {
            setVariable(BR.podcast, podcast)
            setVariable(BR.episode, episode)
            setVariable(BR.handler, viewModel)
        }

        viewModel.apply {
            // get last episode
            playEpisodeEvent.observe(this@EpisodeDetailDialog, Observer { episode ->
                episode?.let {
                    MediaControllerCompat.getMediaController(this@EpisodeDetailDialog.activity!!)
                            .transportControls.playFromMediaId(episode.episodeId, null)

                    //MediaControllerCompat.getMediaController(this@EpisodeDetailDialog.activity)
                    //        .transportControls.sendCustomAction()
                }
            })
        }
    }
}