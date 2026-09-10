package com.example.fieldops.data.repository

import androidx.room.withTransaction
import com.example.fieldops.data.local.ActivityHistoryEntity
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.SyncOperationEntity
import com.example.fieldops.data.local.toActivityHistoryItem
import com.example.fieldops.data.local.toEntity
import com.example.fieldops.data.local.toNotification
import com.example.fieldops.data.local.toWorkOrder
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.ui.activity.ActivityHistoryItem
import com.example.fieldops.ui.notification.FieldOpsNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkOrderRepository(
    private val database: FieldOpsDatabase
) {

    private val workOrderDao =
        database.workOrderDao()

    private val syncOperationDao =
        database.syncOperationDao()

    private val activityHistoryDao =
        database.activityHistoryDao()

    private val notificationDao =
        database.notificationDao()

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

    fun observeActivityHistory():
            Flow<List<ActivityHistoryItem>> {

        return activityHistoryDao
            .observeActivities()
            .map { entities ->
                entities.map { entity ->
                    entity.toActivityHistoryItem()
                }
            }
    }

    fun observeNotifications():
            Flow<List<FieldOpsNotification>> {

        return notificationDao
            .observeNotifications()
            .map { entities ->
                entities.map { entity ->
                    entity.toNotification()
                }
            }
    }

    suspend fun addActivity(
        activity: ActivityHistoryItem
    ) {

        activityHistoryDao.insertActivity(
            activity.toEntity()
        )
    }

    suspend fun addActivities(
        activities: List<ActivityHistoryItem>
    ) {

        if (activities.isEmpty()) {
            return
        }

        activityHistoryDao.insertActivities(
            activities.map { activity ->
                activity.toEntity()
            }
        )
    }

    suspend fun clearActivityHistory() {

        activityHistoryDao.deleteAllActivities()
    }

    suspend fun addNotification(
        notification: FieldOpsNotification
    ) {

        notificationDao.insertNotification(
            notification.toEntity()
        )
    }

    suspend fun addNotifications(
        notifications: List<FieldOpsNotification>
    ) {

        if (notifications.isEmpty()) {
            return
        }

        notificationDao.insertNotifications(
            notifications.map { notification ->
                notification.toEntity()
            }
        )
    }

    suspend fun markNotificationRead(
        id: String
    ) {

        notificationDao.markAsRead(
            id
        )
    }

    suspend fun markAllNotificationsRead() {

        notificationDao.markAllAsRead()
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

    suspend fun refreshFromServer(): Result<List<WorkOrder>> {

        if (!com.example.fieldops.data.remote.RetrofitClient.isConfigured) {
            return Result.failure(
                IllegalStateException(
                    "Backend API belum dikonfigurasi."
                )
            )
        }

        return runCatching {
            val remoteWorkOrders =
                com.example.fieldops.data.remote.RetrofitClient.api
                    .getWorkOrders()

            val entities =
                remoteWorkOrders.map { workOrder ->
                    com.example.fieldops.data.local.WorkOrderEntity(
                        id = workOrder.id,
                        title = workOrder.title,
                        customer = workOrder.customer,
                        location = workOrder.location,
                        date = workOrder.date,
                        time = workOrder.time,
                        status = workOrder.status,
                        priority = workOrder.priority,
                        assetId = workOrder.assetId
                    )
                }

            database.withTransaction {
                workOrderDao.deleteAllWorkOrders()

                if (entities.isNotEmpty()) {
                    workOrderDao.insertWorkOrders(entities)
                }
            }

            remoteWorkOrders.map { workOrder ->
                com.example.fieldops.data.model.WorkOrder(
                    id = workOrder.id,
                    title = workOrder.title,
                    customer = workOrder.customer,
                    location = workOrder.location,
                    date = workOrder.date,
                    time = workOrder.time,
                    status = com.example.fieldops.data.model.WorkOrderStatus.entries.firstOrNull {
                        it.name.equals(
                            workOrder.status,
                            ignoreCase = true
                        )
                    } ?: com.example.fieldops.data.model.WorkOrderStatus.PENDING,
                    priority = workOrder.priority,
                    assetId = workOrder.assetId
                )
            }
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