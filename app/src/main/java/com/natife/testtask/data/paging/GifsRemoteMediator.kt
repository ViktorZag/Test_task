package com.natife.testtask.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.natife.testtask.data.local.AppDatabase
import com.natife.testtask.data.local.model.DeletedGifEntity
import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.local.model.KeysEntity
import com.natife.testtask.data.mappers.toGifEntityList
import com.natife.testtask.data.remote.GiphyApi

@OptIn(ExperimentalPagingApi::class)
class GifsRemoteMediator(
    private val api: GiphyApi,
    private val database: AppDatabase
) : RemoteMediator<Int, GifEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GifEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }
            val deletedGifs = database.deletedGifsDao.getDeletedGifs()
            val response = api.getTrendingGifs(
                limit = state.config.pageSize,
                offset = state.config.pageSize * currentPage
            )
            val endOfPaginationReached = response.data.isEmpty()

            val filteredResponse = response.toGifEntityList().filterNot {
                deletedGifs.contains(DeletedGifEntity(it.id))
            }
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.gifsDao.deleteAllGifs()
                    database.keysDao.deleteAllGifKeys()
                }
                val keys = filteredResponse.map { gifDto ->
                    KeysEntity(
                        id = gifDto.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                database.keysDao.addAllGifKeys(keys = keys)
                database.gifsDao.addGifs(gifs = filteredResponse)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }


    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, GifEntity>
    ): KeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.keysDao.getGifKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, GifEntity>
    ): KeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { gifEntity ->
                database.keysDao.getGifKeys(id = gifEntity.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, GifEntity>
    ): KeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { gifEntity ->
                database.keysDao.getGifKeys(id = gifEntity.id)
            }
    }
}
