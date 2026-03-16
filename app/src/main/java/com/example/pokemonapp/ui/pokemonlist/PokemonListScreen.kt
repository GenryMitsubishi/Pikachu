package com.example.pokemonapp.ui.pokemonlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemonapp.data.model.PokemonListItem

private enum class ListTab { ALL, FAVOURITES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(ListTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                viewModel.onEvent(PokemonListEvent.Search(query))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search Pokemon...") },
            singleLine = true
        )

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == ListTab.ALL,
                onClick = { selectedTab = ListTab.ALL },
                text = { Text("Все") }
            )
            Tab(
                selected = selectedTab == ListTab.FAVOURITES,
                onClick = { selectedTab = ListTab.FAVOURITES },
                text = { Text("Избранное") }
            )
        }

        when (val state = uiState) {
            is PokemonListUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PokemonListUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.onEvent(PokemonListEvent.Retry) }
                )
            }
            is PokemonListUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Pokemon found",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            is PokemonListUiState.Success -> {
                val listToShow = when (selectedTab) {
                    ListTab.ALL -> state.filteredList
                    ListTab.FAVOURITES -> state.filteredList.filter { it.id in state.favourites }
                }
                if (selectedTab == ListTab.FAVOURITES && listToShow.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет избранных покемонов",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    PokemonListContent(
                        pokemonList = listToShow,
                        favourites = state.favourites,
                        onPokemonClick = onPokemonClick,
                        onToggleFavourite = { id -> viewModel.onEvent(PokemonListEvent.ToggleFavourite(id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonListContent(
    pokemonList: List<PokemonListItem>,
    favourites: Set<Int>,
    onPokemonClick: (Int) -> Unit,
    onToggleFavourite: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pokemonList) { pokemon ->
            PokemonListItem(
                pokemon = pokemon,
                isFavourite = favourites.contains(pokemon.id),
                onClick = { onPokemonClick(pokemon.id) },
                onFavouriteClick = { onToggleFavourite(pokemon.id) }
            )
        }
    }
}

@Composable
fun PokemonListItem(
    pokemon: PokemonListItem,
    isFavourite: Boolean,
    onClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "#${pokemon.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onFavouriteClick() }) {
                Icon(
                    imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                    tint = if (isFavourite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
