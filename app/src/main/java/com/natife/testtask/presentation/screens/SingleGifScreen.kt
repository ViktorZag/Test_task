package com.natife.testtask.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.natife.testtask.R
import com.natife.testtask.presentation.viewModels.GifsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SingleGifScreen(
    imageLoader: ImageLoader,
    page: Int,
    viewModel: GifsViewModel = hiltViewModel(),
    onBackPress: (Int) -> Unit
) {
    val gifs = viewModel.gifsFlow.collectAsLazyPagingItems()

    val pagerState = rememberPagerState(initialPage = page, initialPageOffsetFraction = 0f) {
        gifs.itemCount
    }
    BackHandler {
        onBackPress(pagerState.currentPage)
    }

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp
        ) { page ->
            AsyncImage(
                imageLoader = imageLoader,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gifs[page]?.originalUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}