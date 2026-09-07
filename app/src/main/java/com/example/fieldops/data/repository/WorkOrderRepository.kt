package com.example.fieldops.data.repository

import androidx.room.withTransaction
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.SyncOperationEntity
import com.example.fieldops.data.local.toEntity
import com.example.fieldops.data.local.toWorkOrder
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.demoWorkOrders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkOrderRepository(
    private val database: FieldOpsDatabase
) {

    private val workOrderDao =
        database.workOrderDao()

    private val syncOperationDao =
        database.syncOperationDao()

    fun observeWorkOrders(): Flow<List<WorkOrder>> {
        return workOrderDao
            .observeWorkOrders()
            .map { entities ->
                entities.map { entity ->
                    entity.toWorkOrder()
                }
            }
    }

    fun observeWorkOrder(
        id: String
    ): Flow<WorkOrder?> {
        return workOrderDao
            .observeWorkOrder(id)
            .map { entity ->
                entity?.toWorkOrder()
            }
    }

    fun observePendingSyncOperations():
            Flow<List<SyncOperationEntity>> {

        return syncOperationDao
            .observePendingOperations()
    }

    suspend fun updateWorkOrder(
        workOrder: WorkOrder
    ) {

        database.withTransaction {

            workOrderDao.insertWorkOrder(
                workOrder.toEntity()
            )

            syncOperationDao.insertOperation(
                SyncOperationEntity(
                    workOrderId = workOrder.id,
                    operation = workOrder.status.name,
                    createdAt = System.currentTimeMillis(),
                    status = "PENDING"
                )
            )
        }
    }

    suspend fun updateWorkOrders(
        workOrders: List<WorkOrder>
    ) {

        if (workOrders.isEmpty()) {
            return
        }

        workOrderDao.insertWorkOrders(
            workOrders.map { workOrder ->
                workOrder.toEntity()
            }
        )
    }

    suspend fun seedDemoDataIfNeeded() {

        if (workOrderDao.getWorkOrderCount() == 0) {
            updateWorkOrders(
                demoWorkOrders
            )
        }
    }

    suspend fun deleteWorkOrder(
        workOrder: WorkOrder
    ) {

        database.withTransaction {

            workOrderDao.deleteWorkOrder(
                workOrder.toEntity()
            )
        }
    }

    suspend fun clearAllWorkOrders() {

        database.withTransaction {

            workOrderDao.deleteAllWorkOrders()
            syncOperationDao.deleteAllOperations()
        }
    }

    suspend fun getPendingSyncOperations():
            List<SyncOperationEntity> {

        return syncOperationDao
            .getPendingOperations()
    }

    suspend fun markSyncOperationCompleted(
        id: Long
    ) {

        syncOperationDao.updateStatus(
            id = id,
            status = "COMPLETED"
        )
    }
}