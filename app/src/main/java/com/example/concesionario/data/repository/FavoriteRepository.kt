package com.example.concesionario.data.repository

import com.example.concesionario.data.local.FavoriteDao
import com.example.concesionario.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {

    val allFavorites: Flow<List<FavoriteEntity>> =
        favoriteDao.getAllFavorites()

    fun isFavorite(vehicleId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(vehicleId)
    }

    suspend fun addFavorite(vehicleId: Int) {
        favoriteDao.addFavorite(
            FavoriteEntity(vehicleId = vehicleId)
        )
    }

    suspend fun removeFavorite(vehicleId: Int) {
        favoriteDao.removeFavorite(vehicleId)
    }
}
