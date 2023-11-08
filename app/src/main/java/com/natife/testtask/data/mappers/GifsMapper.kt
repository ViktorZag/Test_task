package com.natife.testtask.data.mappers

import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.remote.model.GiphyResponseDto

fun GiphyResponseDto.toGifEntityList(): List<GifEntity> {
    return this.data.map { data ->
        GifEntity(
            id = data.id,
            title = data.title,
            fixedHeightUrl = data.images.fixed_height.url,
            originalUrl = data.images.original.url,
            slug = data.slug
        )
    }
}