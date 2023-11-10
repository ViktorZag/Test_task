package com.natife.testtask.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import com.natife.testtask.data.local.AppDatabase
import com.natife.testtask.data.local.model.DeletedGifEntity
import com.natife.testtask.data.mappers.toGifCardItem
import com.natife.testtask.data.paging.GifsRemoteMediator
import com.natife.testtask.data.paging.SearchPagingSource
import com.natife.testtask.data.remote.GiphyApi
import com.natife.testtask.presentation.models.GifCardItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class GifsRepositoryImpl @Inject constructor(
    private val giphyApi: GiphyApi,
    private val database: AppDatabase,
    private val imageLoader: ImageLoader
) : GifsRepository {

    override fun getTrendingGifs(): Flow<PagingData<GifCardItem>> {
        val pagingSourceFactory = { database.gifsDao.getAllGifs() }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = GifsRemoteMediator(
                api = giphyApi,
                database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map {
                it.map { gifEntity ->
                    gifEntity.toGifCardItem()
                }
            }
    }

    override fun searchGifs(query: String): Flow<PagingData<GifCardItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                SearchPagingSource(
                    api = giphyApi,
                    query = query
                )
            }
        ).flow
            .map {
                it.map {
                    it.toGifCardItem()
                }
            }
    }

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun deleteGif(gifCardItem: GifCardItem) {
        imageLoader.diskCache?.remove(gifCardItem.lowResolutionUrl)
        imageLoader.diskCache?.remove(gifCardItem.hightResolutionUrl)
        imageLoader.memoryCache?.remove(MemoryCache.Key(gifCardItem.lowResolutionUrl))
        imageLoader.memoryCache?.remove(MemoryCache.Key(gifCardItem.hightResolutionUrl))

        database.withTransaction {
            database.gifsDao.deleteGif(gifCardItem.id)
            database.keysDao.deleteGifKey(gifCardItem.id)
            database.deletedGifsDao.addDeletedGif(DeletedGifEntity(gifCardItem.id))
        }
    }

}