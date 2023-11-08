package com.natife.testtask.presentation.navigation

sealed class Destination(
    val route: String,
    val argName: String?
) {

    object ListScreen : Destination(route = "list_screen", null)
    object SingleGifScreen : Destination(route = "single_gif_screen", "position")
}


