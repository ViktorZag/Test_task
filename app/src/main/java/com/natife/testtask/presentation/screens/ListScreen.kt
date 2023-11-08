package com.natife.testtask.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.natife.testtask.R
import com.natife.testtask.presentation.theme.dimens
import com.natife.testtask.presentation.viewModels.GifsViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ListScreen(
    imageLoader: ImageLoader,
    viewModel: GifsViewModel = hiltViewModel(),
    onGifClick: (Int) -> Unit,
    initialPosition: StateFlow<Int?>
) {
    val gifsList = viewModel.gifsFlow.collectAsLazyPagingItems()
    val queryState = viewModel.searchQuery.collectAsState()
    val currentPosition = initialPosition.collectAsState()

    val listState = rememberSaveable(
        initialPosition,
        gifsList,
        saver = LazyListState.Saver
    ) {
        if (currentPosition.value == null) LazyListState()
        else LazyListState(currentPosition.value!!)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.dimens.medium),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedField(value = queryState.value,
            onValueChange = { viewModel.updateQuery(it) })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
            content = {
                itemsIndexed(gifsList, key = { _, item -> item.id }) { index, gifEntity ->
                    if (gifEntity != null)
                        AsyncImage(
                            imageLoader = imageLoader,
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(gifEntity.fixedHeightUrl)
                                .placeholder(R.drawable.ic_launcher_background)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGifClick(index) }
                        )

                }
            }
        )

    }
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