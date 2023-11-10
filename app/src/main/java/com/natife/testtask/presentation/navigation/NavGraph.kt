package com.natife.testtask.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.natife.testtask.presentation.screens.ListScreen
import com.natife.testtask.presentation.screens.SingleGifScreen
import com.natife.testtask.presentation.viewModels.GifsViewModel

@Composable
fun SetNavGraph(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: GifsViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Destination.ListScreen.route
    ) {
        composable(Destination.ListScreen.route) {
            ListScreen(
                gifsPagingItems = viewModel.gifsFlow,
                uiStateFlow = viewModel.uiState,
                onGifClick = { index ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("index", index)
                    navController.navigate("${Destination.SingleGifScreen.route}/$index") {
                        launchSingleTop = true
                    }
                },
                initialPosition = it.savedStateHandle.getStateFlow("index",0),
                updateQuery = viewModel::updateQuery,
                onGifDelete = viewModel::deleteGif
            )
        }
        composable(
            "${Destination.SingleGifScreen.route}/{${Destination.SingleGifScreen.argName}}",
            arguments = listOf(navArgument(Destination.SingleGifScreen.argName!!) {
                type = NavType.IntType
            })
        ) {
            SingleGifScreen(
                gifsFlow = viewModel.gifsFlow,
                page = it.arguments?.getInt(Destination.SingleGifScreen.argName) ?: 0,
                onBackPress = { index ->
                    navController.popBackStack()
                    navController.currentBackStackEntry?.savedStateHandle?.set("index", index)
                }
            )
        }
    }
}