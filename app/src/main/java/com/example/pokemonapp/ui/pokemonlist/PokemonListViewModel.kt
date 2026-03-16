package com.example.pokemonapp.ui.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.data.model.PokemonListItem
import com.example.pokemonapp.data.repository.FavouritesRepository
import com.example.pokemonapp.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val favouritesRepository: FavouritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private var allPokemon: List<PokemonListItem> = emptyList()
    private var favourites: Set<Int> = emptySet()
    private var searchJob: Job? = null

    init {
        loadPokemonList()
    }

    fun onEvent(event: PokemonListEvent) {
        when (event) {
            is PokemonListEvent.Search -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300L)
                    searchPokemon(event.query)
                }
            }
            is PokemonListEvent.ToggleFavourite -> {
                toggleFavourite(event.pokemonId)
            }
            is PokemonListEvent.Retry -> {
                loadPokemonList()
            }
        }
    }

    private fun loadPokemonList() {
        _uiState.value = PokemonListUiState.Loading
        viewModelScope.launch {
            val listResult = repository.getPokemonList()
            listResult.fold(
                onSuccess = { list ->
                    allPokemon = list
                    favourites = favouritesRepository.getAllFavourites()
                    val searchQuery = if (_uiState.value is PokemonListUiState.Success) {
                        (_uiState.value as PokemonListUiState.Success).searchQuery
                    } else ""
                    val filteredList = filterList(list, searchQuery)
                    _uiState.value = PokemonListUiState.Success(
                        pokemonList = list,
                        filteredList = filteredList,
                        searchQuery = searchQuery,
                        favourites = favourites
                    )
                },
                onFailure = { error ->
                    _uiState.value = PokemonListUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    private fun searchPokemon(query: String) {
        val filteredList = filterList(allPokemon, query)
        _uiState.value = when {
            filteredList.isEmpty() && query.isNotEmpty() -> PokemonListUiState.Empty
            else -> PokemonListUiState.Success(
                pokemonList = allPokemon,
                filteredList = filteredList,
                searchQuery = query,
                favourites = favourites
            )
        }
    }

    private fun filterList(list: List<PokemonListItem>, query: String): List<PokemonListItem> {
        return if (query.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    private fun toggleFavourite(pokemonId: Int) {
        viewModelScope.launch {
            val isNowFavourite = favouritesRepository.toggleFavourite(pokemonId)
            favourites = if (isNowFavourite) favourites + pokemonId else favourites - pokemonId
            if (_uiState.value is PokemonListUiState.Success) {
                val currentState = _uiState.value as PokemonListUiState.Success
                _uiState.value = currentState.copy(favourites = favourites)
            }
        }
    }
}

sealed class PokemonListEvent {
    data class Search(val query: String) : PokemonListEvent()
    data class ToggleFavourite(val pokemonId: Int) : PokemonListEvent()
    object Retry : PokemonListEvent()
}
