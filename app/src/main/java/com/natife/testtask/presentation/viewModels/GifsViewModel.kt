package com.natife.testtask.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.natife.testtask.data.GifsRepository
import com.natife.testtask.presentation.models.GifCardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val searchQuery: String = "",
    val isDeletingAllowed: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class GifsViewModel @Inject constructor(
    private val repository: GifsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState
        get() = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val gifsFlow: Flow<PagingData<GifCardItem>> = uiState
        .distinctUntilChangedBy { it.searchQuery }
        .debounce(1500)
        .flatMapLatest { uiState ->
            if (uiState.searchQuery.isBlank()) {
                _uiState.update { it.copy(isDeletingAllowed = true) }
                repository.getTrendingGifs()
            } else {
                _uiState.update { it.copy(isDeletingAllowed = false) }
                repository.searchGifs(uiState.searchQuery.trim())
            }
        }
        .cachedIn(viewModelScope)

    fun updateQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteGif(gifCardItem: GifCardItem) {
        viewModelScope.launch {
            repository.deleteGif(gifCardItem)
        }
    }


}