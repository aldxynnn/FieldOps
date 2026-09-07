package com.example.fieldops.ui.workorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.repository.WorkOrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkOrderViewModel(
    private val repository: WorkOrderRepository
) : ViewModel() {

    val workOrders: StateFlow<List<WorkOrder>> =
        repository
            .observeWorkOrders()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    5_000L
                ),
                initialValue = emptyList()
            )

    val pendingSyncCount: StateFlow<Int> =
        repository
            .observePendingSyncOperations()
            .map { operations ->
                operations.size
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    5_000L
                ),
                initialValue = 0
            )

    fun updateWorkOrder(
        workOrder: WorkOrder
    ) {
        viewModelScope.launch {
            repository.updateWorkOrder(
                workOrder
            )
        }
    }

    fun deleteWorkOrder(
        workOrder: WorkOrder
    ) {
        viewModelScope.launch {
            repository.deleteWorkOrder(
                workOrder
            )
        }
    }

    fun refreshSeedData() {
        viewModelScope.launch {
            repository.seedDemoDataIfNeeded()
        }
    }

    class Factory(
        private val repository: WorkOrderRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            if (
                modelClass.isAssignableFrom(
                    WorkOrderViewModel::class.java
                )
            ) {
                return WorkOrderViewModel(
                    repository
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}