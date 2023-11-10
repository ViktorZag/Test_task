package com.natife.testtask.data.mappers

import com.natife.testtask.data.local.model.GifEntity
import com.natife.testtask.data.remote.model.GiphyResponseDto
import com.natife.testtask.presentation.models.GifCardItem

fun GiphyResponseDto.toGifEntityList(): List<GifEntity> {
    return this.data.map { data ->
        GifEntity(
            id = data.id,
            title = data.title,
            fixedHeightUrl = data.images.fixed_height.url,
            fixedHeightAspectRatio = data.images.fixed_height.width.toFloat() / data.images.fixed_height.height.toFloat(),
            originalUrl = data.images.original.url,
            slug = data.slug
        )
    }
}

fun List<GifEntity>.toGifCardItems(): List<GifCardItem> {
    return this.map { gifEntity ->
        GifCardItem(
            id = gifEntity.id,
            lowResolutionUrl = gifEntity.fixedHeightUrl,
            lowResolutionAspectRatio = gifEntity.fixedHeightAspectRatio,
            hightResolutionUrl = gifEntity.originalUrl
        )
    }
}

fun GifEntity.toGifCardItem(): GifCardItem {
    return GifCardItem(
        id = this.id,
        lowResolutionUrl = this.fixedHeightUrl,
        lowResolutionAspectRatio = this.fixedHeightAspectRatio,
        hightResolutionUrl = this.originalUrl
    )
}