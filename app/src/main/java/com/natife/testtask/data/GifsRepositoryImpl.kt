package com.natife.testtask.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.natife.testtask.data.local.AppDatabase
import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.paging.GifsRemoteMediator
import com.natife.testtask.data.paging.SearchPagingSource
import com.natife.testtask.data.remote.GiphyApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class GifsRepositoryImpl @Inject constructor(
    private val giphyApi: GiphyApi,
    private val database: AppDatabase
) : GifsRepository {

    override fun getTrendingGifs(): Flow<PagingData<GifEntity>> {
        val pagingSourceFactory = { database.gifsDao.getAllGifs() }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = GifsRemoteMediator(
                api = giphyApi,
                database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun searchGifs(query: String): Flow<PagingData<GifEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchPagingSource(api = giphyApi, query = query)
            }
        ).flow
    }
}