package com.natife.testtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.natife.testtask.data.local.model.KeysEntity
import com.natife.testtask.util.Constants.KEYS_TABLE_NAME

@Dao
interface KeysDao {

    @Query("SELECT * FROM $KEYS_TABLE_NAME WHERE id =:id")
    suspend fun getGifKeys(id: String): KeysEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllGifKeys(remoteKeys: List<KeysEntity>)

    @Query("DELETE FROM $KEYS_TABLE_NAME")
    suspend fun deleteAllGifKeys()

}