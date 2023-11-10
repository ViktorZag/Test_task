package com.natife.testtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.natife.testtask.data.local.model.DeletedGifEntity
import com.natife.testtask.util.Constants.DELETED_GIFS_TABLE_NAME

@Dao
interface DeletedGifsDao {

    @Query("SELECT * FROM $DELETED_GIFS_TABLE_NAME")
    suspend fun getDeletedGifs(): List<DeletedGifEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDeletedGif(deletedGif: DeletedGifEntity)
}