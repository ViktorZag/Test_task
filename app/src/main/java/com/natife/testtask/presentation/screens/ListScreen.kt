package com.natife.testtask.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.natife.testtask.R
import com.natife.testtask.presentation.models.GifCardItem
import com.natife.testtask.presentation.theme.dimens
import com.natife.testtask.presentation.viewModels.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ListScreen(
    gifsPagingItems: Flow<PagingData<GifCardItem>>,
    uiStateFlow: StateFlow<UiState>,
    onGifClick: (Int) -> Unit,
    initialPosition: StateFlow<Int>,
    onGifDelete: (GifCardItem) -> Unit,
    updateQuery: (String) -> Unit
) {
    val gifsList = gifsPagingItems.collectAsLazyPagingItems()
    val uiState = uiStateFlow.collectAsState()

    val listState = rememberSaveable(
        initialPosition,
        gifsList,
        saver = LazyListState.Saver
    ) {
        LazyListState(initialPosition.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.dimens.large),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedField(value = uiState.value.searchQuery,
            onValueChange = { updateQuery(it) })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = listState,
            content = {
                itemsIndexed(gifsList, key = { _, item -> item.id }) { index, gifCardItem ->
                    if (gifCardItem != null) {
                        if (uiState.value.isDeletingAllowed) {
                            GifDeletingListItem(gifCardItem = gifCardItem,
                                onGifClick = { onGifClick(index) },
                                onGifDelete = { onGifDelete(gifCardItem) })
                        } else {
                            GifListItem(gifCardItem = gifCardItem,
                                onGifClick = { onGifClick(index) })
                        }
                    }
                }
            }
        )

    }
}

@Composable
private fun GifListItem(
    gifCardItem: GifCardItem,
    onGifClick: () -> Unit
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gifCardItem.lowResolutionUrl)
            .placeholder(R.drawable.image_placeholder_24)
            .error(R.drawable.image_no_internet_24)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(gifCardItem.lowResolutionAspectRatio)
            .clickable { onGifClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.GifDeletingListItem(
    gifCardItem: GifCardItem,
    onGifClick: () -> Unit,
    onGifDelete: () -> Unit
) {

    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onGifDelete()
                true
            } else false
        }, positionalThreshold = { 180.dp.toPx() }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier
            .animateItemPlacement()
            .padding(vertical = Dp(1f)),
        directions = setOf(
            DismissDirection.EndToStart
        ),
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.White
                    else -> Color.Red
                }
            )
            val alignment = Alignment.CenterEnd
            val icon = Icons.Default.Delete

            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = Dp(20f)),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = "Delete Icon",
                    modifier = Modifier.scale(scale)
                )
            }
        },
        dismissContent = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gifCardItem.lowResolutionUrl)
                    .placeholder(R.drawable.image_placeholder_24)
                    .error(R.drawable.image_no_internet_24)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(gifCardItem.lowResolutionAspectRatio)
                    .clickable { onGifClick() }
            )
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    padding: Dp = MaterialTheme.dimens.medium,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = {
            Text(
                text = "Search",
                style = MaterialTheme.typography.bodySmall
            )
        },
        placeholder = {
            Text(
                text = "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(start = padding, top = padding, end = padding),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            autoCorrect = true,
            capitalization = KeyboardCapitalization.None
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            },
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            disabledPlaceholderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
        ),
    )

}