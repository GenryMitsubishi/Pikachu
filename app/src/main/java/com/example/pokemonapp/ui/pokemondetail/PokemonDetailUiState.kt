package com.example.pokemonapp.ui.pokemondetail

import com.example.pokemonapp.data.model.PokemonDetail

sealed class PokemonDetailUiState {
    object Loading : PokemonDetailUiState()
    data class Error(val message: String) : PokemonDetailUiState()
    data class Success(
        val pokemon: PokemonDetail
    ) : PokemonDetailUiState()
}
