package com.caldeirasoft.basicapp.domain.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.caldeirasoft.basicapp.data.datasources.remote.ITunesApi
import com.caldeirasoft.basicapp.data.datasources.local.dao.PodcastDao
import com.caldeirasoft.basicapp.domain.entity.Podcast

/**
 * Created by Edmond on 09/02/2018.
 */
abstract class ItunesLookupDataSourceFactory : DataSource.Factory<Int, Podcast>()
{
    abstract var isLoading: LiveData<Boolean>
    abstract var networkErrors: LiveData<String>
}
