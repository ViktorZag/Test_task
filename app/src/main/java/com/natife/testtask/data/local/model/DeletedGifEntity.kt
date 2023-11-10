package com.natife.testtask.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.natife.testtask.util.Constants.DELETED_GIFS_TABLE_NAME

@Entity(tableName = DELETED_GIFS_TABLE_NAME)
data class DeletedGifEntity(
    @PrimaryKey
    val id: String
)