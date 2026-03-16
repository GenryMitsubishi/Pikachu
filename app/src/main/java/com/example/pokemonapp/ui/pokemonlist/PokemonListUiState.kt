package com.example.pokemonapp.ui.pokemonlist

import com.example.pokemonapp.data.model.PokemonListItem

sealed class PokemonListUiState {
    object Loading : PokemonListUiState()
    data class Error(val message: String) : PokemonListUiState()
    data class Success(
        val pokemonList: List<PokemonListItem>,
        val filteredList: List<PokemonListItem>,
        val searchQuery: String = "",
        val favourites: Set<Int> = emptySet()
    ) : PokemonListUiState()
    object Empty : PokemonListUiState()
}
