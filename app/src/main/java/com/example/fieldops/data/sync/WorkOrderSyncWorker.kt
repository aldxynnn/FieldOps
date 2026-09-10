package com.example.fieldops.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.withTransaction
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.WorkOrderEntity
import com.example.fieldops.data.remote.RetrofitClient
import com.example.fieldops.data.remote.StatusUpdateRequest

class WorkOrderSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    override suspend fun doWork(): Result {

        val database =
            FieldOpsDatabase.getInstance(
                applicationContext
            )

        if (!RetrofitClient.isConfigured) {
            return Result.success()
        }

        if (!com.example.fieldops.data.remote.SessionManager(applicationContext).isLoggedIn()) {
            return Result.success()
        }

        return try {
            val syncDao = database.syncOperationDao()

            val pendingOperations =
                syncDao.getPendingOperations()

            for (operation in pendingOperations) {
                try {
                    RetrofitClient.api.updateWorkOrderStatus(
                        id = operation.workOrderId,
                        request = StatusUpdateRequest(
                            status = operation.operation
                        )
                    )

                    syncDao.updateStatus(
                        id = operation.id,
                        status = "COMPLETED"
                    )
                } catch (exception: Exception) {
                    return Result.retry()
                }
            }

            val remoteWorkOrders =
                RetrofitClient.api.getWorkOrders()

            val remainingPendingIds =
                syncDao.getPendingOperations()
                    .map { it.workOrderId }
                    .toSet()

            val entities =
                remoteWorkOrders
                    .filter { workOrder ->
                        workOrder.id !in remainingPendingIds
                    }
                    .map { workOrder ->
                        WorkOrderEntity(
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
                database.workOrderDao()
                    .deleteAllWorkOrders()

                if (entities.isNotEmpty()) {
                    database.workOrderDao()
                        .insertWorkOrders(entities)
                }
            }

            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }
}
