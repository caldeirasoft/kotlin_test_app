package com.caldeirasoft.basicapp.ui.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.ui.episodes.EpisodesViewModel
import com.caldeirasoft.basicapp.util.LoadingState

interface IListViewModel<T> {
    var loadingState : LiveData<LoadingState>
}