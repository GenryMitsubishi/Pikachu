package com.example.pokemonapp.data.repository

import com.example.pokemonapp.data.api.PokemonApiService
import com.example.pokemonapp.data.model.PokemonDetail
import com.example.pokemonapp.data.model.PokemonListItem
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val apiService: PokemonApiService
) {

    suspend fun getPokemonList(): Result<List<PokemonListItem>> {
        return try {
            val response = apiService.getPokemonList(limit = 1000)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPokemonDetail(id: Int): Result<PokemonDetail> {
        return try {
            val response = apiService.getPokemonDetail(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
