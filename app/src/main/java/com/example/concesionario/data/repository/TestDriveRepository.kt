package com.example.concesionario.data.repository

import com.example.concesionario.data.local.TestDriveDao
import com.example.concesionario.data.local.TestDriveEntity
import kotlinx.coroutines.flow.Flow

class TestDriveRepository(
    private val testDriveDao: TestDriveDao
) {

    val allTestDrives: Flow<List<TestDriveEntity>> =
        testDriveDao.getAllTestDrives()

    suspend fun addTestDrive(testDrive: TestDriveEntity) {
        testDriveDao.insertTestDrive(testDrive)
    }

    suspend fun getTestDriveById(testDriveId: Int): TestDriveEntity? {
        return testDriveDao.getTestDriveById(testDriveId)
    }

    suspend fun deleteTestDrive(testDriveId: Int) {
        testDriveDao.deleteTestDrive(testDriveId)
    }
}
