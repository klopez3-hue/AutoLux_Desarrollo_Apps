package com.example.concesionario.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDriveDao {

    @Insert
    suspend fun insertTestDrive(testDrive: TestDriveEntity)

    @Update
    suspend fun updateTestDrive(testDrive: TestDriveEntity)

    @Query("SELECT * FROM test_drives ORDER BY createdAt DESC")
    fun getAllTestDrives(): Flow<List<TestDriveEntity>>

    @Query("SELECT * FROM test_drives WHERE id = :testDriveId")
    suspend fun getTestDriveById(testDriveId: Int): TestDriveEntity?

    @Query("DELETE FROM test_drives WHERE id = :testDriveId")
    suspend fun deleteTestDrive(testDriveId: Int)

}
