package com.caldeirasoft.castly.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.caldeirasoft.castly.domain.model.entities.NetworkState
import com.caldeirasoft.castly.domain.model.itunes.GroupingPageData
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.base.Base2UseCase
import com.caldeirasoft.castly.domain.usecase.base.BaseUseCase
import com.caldeirasoft.castly.domain.usecase.base.UseCaseResult
import com.caldeirasoft.castly.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class GetItunesGroupingDataUseCase(val itunesRepository: ItunesRepository) {

    fun execute(storeFront: String, genreId: Int): Flow<Resource<GroupingPageData>> =
            itunesRepository.getGroupingPageData(storeFront, genreId)
}