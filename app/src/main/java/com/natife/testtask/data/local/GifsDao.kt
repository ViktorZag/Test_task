package com.natife.testtask.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.util.Constants.GIFS_TABLE_NAME

@Dao
interface GifsDao {

    @Query("SELECT * FROM $GIFS_TABLE_NAME")
    fun getAllGifs(): PagingSource<Int, GifEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGifs(gifs: List<GifEntity>)

    @Query("DELETE FROM $GIFS_TABLE_NAME")
    suspend fun deleteAllGifs()

    @Query("DELETE  FROM $GIFS_TABLE_NAME WHERE id =:id")
    suspend fun deleteGif(id: String)
}