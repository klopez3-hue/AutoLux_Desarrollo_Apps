package com.example.concesionario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.concesionario.data.local.AppDatabase
import com.example.concesionario.data.repository.FavoriteRepository
import com.example.concesionario.data.repository.TestDriveRepository

class AutoLuxViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return when {
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(
                    FavoriteRepository(database.favoriteDao())
                ) as T
            }

            modelClass.isAssignableFrom(TestDriveViewModel::class.java) -> {
                TestDriveViewModel(
                    TestDriveRepository(database.testDriveDao())
                ) as T
            }

            else -> throw IllegalArgumentException(
                "ViewModel desconocido: ${modelClass.name}"
            )
        }
    }
}
