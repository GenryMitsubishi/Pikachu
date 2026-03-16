package com.example.pokemonapp.ui.pokemondetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokemonapp.data.model.PokemonDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    favourites: Set<Int>,
    onToggleFavourite: (Int) -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    var uiState by remember { mutableStateOf(viewModel.uiState) }
    
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetail(pokemonId)
        uiState = viewModel.uiState
    }

    LaunchedEffect(viewModel.uiState) {
        uiState = viewModel.uiState
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokemon Details") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is PokemonDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PokemonDetailUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = {
                            viewModel.onEvent(PokemonDetailEvent.Retry)
                            uiState = viewModel.uiState
                        }
                    )
                }
                is PokemonDetailUiState.Success -> {
                    PokemonDetailContent(
                        pokemon = state.pokemon,
                        isFavourite = favourites.contains(pokemonId),
                        onToggleFavourite = { onToggleFavourite(pokemonId) }
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonDetailContent(
    pokemon: PokemonDetail,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        pokemon.sprites.frontDefault?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(200.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onToggleFavourite) {
                Icon(
                    imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                    tint = if (isFavourite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Text(
            text = "#${pokemon.id}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoCard(
                title = "Height",
                value = "${pokemon.height / 10.0} m"
            )
            InfoCard(
                title = "Weight",
                value = "${pokemon.weight / 10.0} kg"
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pokemon.types.forEach { typeSlot ->
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text(typeSlot.type.name.replaceFirstChar { it.uppercaseChar() }) }
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Abilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                pokemon.abilities.forEach { abilitySlot ->
                    Text(
                        text = "${abilitySlot.ability.name.replaceFirstChar { it.uppercaseChar() }}${if (abilitySlot.isHidden) " (Hidden)" else ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                pokemon.stats.forEach { statSlot ->
                    StatRow(
                        statName = statSlot.stat.name.replaceFirstChar { it.uppercaseChar() },
                        value = statSlot.baseStat
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatRow(
    statName: String,
    value: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statName,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
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
