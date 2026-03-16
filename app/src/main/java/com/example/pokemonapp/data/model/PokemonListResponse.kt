package com.example.pokemonapp.data.model

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
) {
    val id: Int
        get() = url.split("/").dropLast(1).last().toIntOrNull() ?: 0
}
