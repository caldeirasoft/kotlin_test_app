package com.caldeirasoft.basicapp.ui.queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.Mockup
import com.caldeirasoft.basicapp.data.db.episodes.EpisodeDataSource
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.data.repository.EpisodeRepository
import com.caldeirasoft.basicapp.ui.common.SingleLiveEvent
import com.caldeirasoft.basicapp.ui.episodelist.EpisodeListViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class QueueViewModel : EpisodeListViewModel(SectionState.QUEUE) {

}