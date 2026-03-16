package com.example.pokemonapp.data.model

import com.google.gson.annotations.SerializedName

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val abilities: List<AbilitySlot>,
    val stats: List<StatSlot>
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class TypeSlot(
    val slot: Int,
    val type: Type
)

data class Type(
    val name: String,
    val url: String
)

data class AbilitySlot(
    val slot: Int,
    val ability: Ability,
    @SerializedName("is_hidden")
    val isHidden: Boolean
)

data class Ability(
    val name: String,
    val url: String
)

data class StatSlot(
    @SerializedName("base_stat")
    val baseStat: Int,
    val effort: Int,
    val stat: Stat
)

data class Stat(
    val name: String,
    val url: String
)
