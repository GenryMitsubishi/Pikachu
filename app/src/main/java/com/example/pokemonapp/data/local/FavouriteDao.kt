package com.example.pokemonapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Query("SELECT pokemon_id FROM favourites")
    suspend fun getAllIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE pokemon_id = :pokemonId")
    suspend fun deleteById(pokemonId: Int)

    @Query("SELECT COUNT(*) FROM favourites WHERE pokemon_id = :pokemonId")
    suspend fun isFavourite(pokemonId: Int): Int
}
