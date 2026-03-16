package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.local.FavouriteDao
import com.example.pokemonapp.data.local.FavouriteEntity
import javax.inject.Inject

class FavouritesRepository @Inject constructor(
    private val favouriteDao: FavouriteDao
) {

    suspend fun getAllFavourites(): Set<Int> {
        return favouriteDao.getAllIds().toSet()
    }

    suspend fun addFavourite(pokemonId: Int) {
        favouriteDao.insert(FavouriteEntity(pokemonId = pokemonId))
    }

    suspend fun removeFavourite(pokemonId: Int) {
        favouriteDao.deleteById(pokemonId)
    }

    suspend fun toggleFavourite(pokemonId: Int): Boolean {
        val isFav = favouriteDao.isFavourite(pokemonId) > 0
        if (isFav) {
            favouriteDao.deleteById(pokemonId)
            return false
        } else {
            favouriteDao.insert(FavouriteEntity(pokemonId = pokemonId))
            return true
        }
    }
}
