package com.natife.testtask.data.remote.model

data class GiphyResponseDto(
    val data: List<Data>
)

data class Data(
    val id: String,
    val images: Images,
    val title: String,
    val slug:String
)

data class Images(
    val fixed_height: FixedHeight,
    val original: Original
)

data class FixedHeight(
    val url: String,
    val height: String,
    val width: String,
    val size: String
)

data class Original(
    val url: String,
    val height: String,
    val width: String,
    val size: String
)

