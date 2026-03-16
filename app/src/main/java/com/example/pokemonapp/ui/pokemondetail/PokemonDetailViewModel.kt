package com.example.pokemonapp.ui.pokemondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var uiState: PokemonDetailUiState = PokemonDetailUiState.Loading
        private set

    private var pokemonId: Int = 0

    fun loadPokemonDetail(id: Int) {
        pokemonId = id
        uiState = PokemonDetailUiState.Loading
        viewModelScope.launch {
            repository.getPokemonDetail(id).fold(
                onSuccess = { pokemon ->
                    uiState = PokemonDetailUiState.Success(
                        pokemon = pokemon
                    )
                },
                onFailure = { error ->
                    uiState = PokemonDetailUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun onEvent(event: PokemonDetailEvent) {
        when (event) {
            is PokemonDetailEvent.Retry -> {
                loadPokemonDetail(pokemonId)
            }
        }
    }
}

sealed class PokemonDetailEvent {
    object Retry : PokemonDetailEvent()
}
