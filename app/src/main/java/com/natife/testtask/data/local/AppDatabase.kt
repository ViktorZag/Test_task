package com.natife.testtask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.natife.testtask.data.local.model.DeletedGifEntity
import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.local.model.KeysEntity

@Database(
    version = 1,
    entities = [GifEntity::class, KeysEntity::class, DeletedGifEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val gifsDao: GifsDao
    abstract val keysDao: KeysDao
    abstract val deletedGifsDao: DeletedGifsDao
}