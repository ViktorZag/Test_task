package com.natife.testtask.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.mappers.toGifEntityList
import com.natife.testtask.data.remote.GiphyApi

class SearchPagingSource(
    private val api: GiphyApi,
    private val query: String
) : PagingSource<Int, GifEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifEntity> {
        val currentPage = params.key ?: 1
        return try {
            val response = api.getSearchGifs(query = query, limit = 20, offset = 20 * currentPage)
            val endOfPaginationReached = response.data.isEmpty()
            if (!endOfPaginationReached) {
                LoadResult.Page(
                    data = response.toGifEntityList(),
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (endOfPaginationReached) null else currentPage + 1
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifEntity>): Int? {
        return state.anchorPosition
    }

}