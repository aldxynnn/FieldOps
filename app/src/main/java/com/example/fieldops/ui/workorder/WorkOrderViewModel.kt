package com.example.fieldops.ui.workorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.network.NetworkConnectivityManager
import com.example.fieldops.data.repository.WorkOrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WorkOrderUiState {
    data object Loading : WorkOrderUiState
    data class Success(
        val workOrders: List<WorkOrder>
    ) : WorkOrderUiState
    data class Error(
        val message: String
    ) : WorkOrderUiState
}

class WorkOrderViewModel(
    private val repository: WorkOrderRepository,
    private val networkConnectivityManager: NetworkConnectivityManager
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )

    val workOrderUiState: StateFlow<WorkOrderUiState> =
        refreshTrigger
            .onStart { emit(Unit) }
            .flatMapLatest {
                repository
                    .observeWorkOrders()
                    .map< List<WorkOrder>, WorkOrderUiState> { workOrders ->
                        WorkOrderUiState.Success(workOrders)
                    }
                    .onStart {
                        emit(WorkOrderUiState.Loading)
                    }
                    .catch { exception ->
                        emit(
                            WorkOrderUiState.Error(
                                exception.message
                                    ?: "Data Work Order tidak dapat dimuat."
                            )
                        )
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = WorkOrderUiState.Loading
            )

    val workOrders: StateFlow<List<WorkOrder>> =
        workOrderUiState
            .map { state ->
                when (state) {
                    WorkOrderUiState.Loading -> emptyList()
                    is WorkOrderUiState.Success -> state.workOrders
                    is WorkOrderUiState.Error -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = emptyList()
            )

    val pendingSyncCount: StateFlow<Int> =
        repository
            .observePendingSyncOperations()
            .map { operations -> operations.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = 0
            )

    val isOnline: StateFlow<Boolean> =
        networkConnectivityManager.isOnline

    fun updateWorkOrder(workOrder: WorkOrder) {
        viewModelScope.launch {
            repository.updateWorkOrder(workOrder)
        }
    }

    fun deleteWorkOrder(workOrder: WorkOrder) {
        viewModelScope.launch {
            repository.deleteWorkOrder(workOrder)
        }
    }


    fun retryLoad() {
        refreshTrigger.tryEmit(Unit)
    }

    override fun onCleared() {
        networkConnectivityManager.unregister()
        super.onCleared()
    }

    class Factory(
        private val repository: WorkOrderRepository,
        private val networkConnectivityManager: NetworkConnectivityManager
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
                    repository = repository,
                    networkConnectivityManager = networkConnectivityManager
                ) as T
            }

            throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}
