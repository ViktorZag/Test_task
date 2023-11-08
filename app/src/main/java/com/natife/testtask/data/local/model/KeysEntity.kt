package com.natife.testtask.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.natife.testtask.util.Constants.KEYS_TABLE_NAME

@Entity(tableName = KEYS_TABLE_NAME)
data class KeysEntity(
    @PrimaryKey
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
)