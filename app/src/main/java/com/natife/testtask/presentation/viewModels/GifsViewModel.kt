package com.natife.testtask.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.natife.testtask.data.GifsRepository
import com.natife.testtask.data.local.model.GifEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class GifsViewModel @Inject constructor(
    private val repository: GifsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery
        get() = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val gifsFlow: StateFlow<PagingData<GifEntity>> = searchQuery
        .debounce(1500)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getTrendingGifs()
            } else {
                repository.searchGifs(query.trim())
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PagingData.empty())

    fun updateQuery(query: String) {
        _searchQuery.update { query }
    }

}