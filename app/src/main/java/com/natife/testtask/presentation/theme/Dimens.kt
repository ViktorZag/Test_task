package com.natife.testtask.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(

    val zero: Dp = 0.dp,
    val smallest: Dp = 2.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,

    val lottieAnimationsSize: Dp = 250.dp,
    val buttonWidth: Dp = 200.dp,
    val gifMinSize: Dp = 100.dp,
)

val LocalDimens = compositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current