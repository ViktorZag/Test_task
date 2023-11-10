package com.natife.testtask.data

import androidx.paging.PagingData
import com.natife.testtask.presentation.models.GifCardItem
import kotlinx.coroutines.flow.Flow

interface GifsRepository {

    fun getTrendingGifs(): Flow<PagingData<GifCardItem>>

    fun searchGifs(query: String): Flow<PagingData<GifCardItem>>

    suspend fun deleteGif(gifEntity: GifCardItem)
}