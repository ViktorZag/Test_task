package com.natife.testtask.data

import androidx.paging.PagingData
import com.natife.testtask.data.local.model.GifEntity
import kotlinx.coroutines.flow.Flow

interface GifsRepository {

    fun getTrendingGifs(): Flow<PagingData<GifEntity>>

    fun searchGifs(query: String): Flow<PagingData<GifEntity>>

}