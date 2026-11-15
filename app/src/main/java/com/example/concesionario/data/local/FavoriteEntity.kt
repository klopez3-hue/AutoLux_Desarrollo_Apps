package com.example.concesionario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val vehicleId: Int,
    val addedAt: Long = System.currentTimeMillis()
)
