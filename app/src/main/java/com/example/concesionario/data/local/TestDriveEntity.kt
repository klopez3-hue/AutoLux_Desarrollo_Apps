package com.example.concesionario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_drives")
data class TestDriveEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val vehicleId: Int,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val createdAt: Long = System.currentTimeMillis()

)
