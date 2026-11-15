package com.example.concesionario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.concesionario.data.repository.FavoriteRepository
import com.example.concesionario.data.local.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteEntity>> =
        repository.allFavorites.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun isFavorite(vehicleId: Int) =
        repository.isFavorite(vehicleId)

    fun addFavorite(vehicleId: Int) {
        viewModelScope.launch {
            repository.addFavorite(vehicleId)
        }
    }

    fun removeFavorite(vehicleId: Int) {
        viewModelScope.launch {
            repository.removeFavorite(vehicleId)
        }
    }
}
