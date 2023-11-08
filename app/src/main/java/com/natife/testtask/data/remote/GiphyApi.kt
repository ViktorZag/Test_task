package com.natife.testtask.data.remote

import com.natife.testtask.data.remote.model.GiphyResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {

    @GET("trending")
    suspend fun getTrendingGifs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("rating") rating: String = "pg",
    ): GiphyResponseDto

    @GET("search")
    suspend fun getSearchGifs(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("rating") rating: String = "pg"
    ): GiphyResponseDto

}