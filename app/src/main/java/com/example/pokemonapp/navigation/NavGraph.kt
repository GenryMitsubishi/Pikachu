package com.example.pokemonapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.pokemonlist.PokemonListEvent
import com.example.pokemonapp.ui.pokemonlist.PokemonListScreen
import com.example.pokemonapp.ui.pokemonlist.PokemonListUiState
import com.example.pokemonapp.ui.pokemonlist.PokemonListViewModel
import com.example.pokemonapp.ui.pokemondetail.PokemonDetailScreen
import com.example.pokemonapp.ui.pokemondetail.PokemonDetailViewModel

sealed class Screen(val route: String) {
    object List : Screen("pokemon_list")
    object Detail : Screen("pokemon_detail/{id}") {
        fun createRoute(id: Int) = "pokemon_detail/$id"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(Screen.List.route) {
            val listViewModel: PokemonListViewModel = hiltViewModel()
            PokemonListScreen(
                viewModel = listViewModel,
                onPokemonClick = { id: Int ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("id") ?: 0
            val listBackStackEntry = navController.getBackStackEntry(Screen.List.route)
            val listViewModel: PokemonListViewModel = hiltViewModel(listBackStackEntry)
            val listUiState by listViewModel.uiState.collectAsState()
            val favourites =
                (listUiState as? PokemonListUiState.Success)?.favourites.orEmpty()

            PokemonDetailScreen(
                pokemonId = pokemonId,
                favourites = favourites,
                onToggleFavourite = { id: Int ->
                    listViewModel.onEvent(PokemonListEvent.ToggleFavourite(id))
                },
                viewModel = hiltViewModel<PokemonDetailViewModel>()
            )
        }
    }
}
