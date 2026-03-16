package com.example.pokemonapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "pokemon_id")
    val pokemonId: Int
)
