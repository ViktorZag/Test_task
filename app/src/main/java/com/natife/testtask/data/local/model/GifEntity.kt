package com.natife.testtask.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.natife.testtask.util.Constants.GIFS_TABLE_NAME

@Entity(tableName = GIFS_TABLE_NAME)
data class GifEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val fixedHeightUrl: String,
    val originalUrl: String,
    val slug:String
)
