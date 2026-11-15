package com.example.concesionario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.concesionario.data.local.TestDriveEntity
import com.example.concesionario.data.repository.TestDriveRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TestDriveViewModel(
    private val repository: TestDriveRepository
) : ViewModel() {

    val testDrives: StateFlow<List<TestDriveEntity>> =
        repository.allTestDrives.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTestDrive(testDrive: TestDriveEntity) {
        viewModelScope.launch {
            repository.addTestDrive(testDrive)
        }
    }

    fun deleteTestDrive(testDriveId: Int) {
        viewModelScope.launch {
            repository.deleteTestDrive(testDriveId)
        }
    }
}
