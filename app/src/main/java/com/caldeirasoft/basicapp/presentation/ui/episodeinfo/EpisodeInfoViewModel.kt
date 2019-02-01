package com.caldeirasoft.basicapp.presentation.ui.episodeinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.caldeirasoft.basicapp.domain.entity.Episode
import com.caldeirasoft.basicapp.domain.entity.SectionState
import com.caldeirasoft.basicapp.domain.repository.EpisodeRepository
import com.caldeirasoft.basicapp.presentation.utils.SingleLiveEvent

class EpisodeInfoViewModel(val episode:Episode,
                           val episodeRepository: EpisodeRepository) : ViewModel() {

    var episodeData = MutableLiveData<Episode>().apply { value = episode }
    val episodeDb: LiveData<Episode>

    val _sectionData: MediatorLiveData<Int> = MediatorLiveData()
    val sectionData: LiveData<Int> = _sectionData

    init {
        episodeDb = episodeRepository.getEpisodeLive(episode.episodeId)

        _sectionData.addSource(episodeDb) { episode ->
            _sectionData.postValue(episode?.section ?: SectionState.ARCHIVE.value)
        }
    }
}