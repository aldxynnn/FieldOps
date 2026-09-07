package com.example.fieldops.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fieldops.data.local.FieldOpsDatabase
import com.example.fieldops.data.local.WorkOrderEntity
import com.example.fieldops.data.remote.RetrofitClient

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

        return try {

            val pendingOperations =
                database.syncOperationDao()
                    .getPendingOperations()

            val pendingWorkOrderIds =
                pendingOperations
                    .map { operation ->
                        operation.workOrderId
                    }
                    .toSet()

            val remoteWorkOrders =
                RetrofitClient.api
                    .getWorkOrders()

            if (remoteWorkOrders.isNotEmpty()) {

                val entities =
                    remoteWorkOrders
                        .filter { workOrder ->
                            workOrder.id !in pendingWorkOrderIds
                        }
                        .map { workOrder ->

                            WorkOrderEntity(
                                id =
                                    workOrder.id,

                                title =
                                    workOrder.title,

                                customer =
                                    workOrder.customer,

                                location =
                                    workOrder.location,

                                date =
                                    workOrder.date,

                                time =
                                    workOrder.time,

                                status =
                                    workOrder.status,

                                priority =
                                    workOrder.priority
                            )
                        }

                if (entities.isNotEmpty()) {

                    database.workOrderDao()
                        .insertWorkOrders(
                            entities
                        )
                }
            }

            /*
             * Pending operations sengaja belum ditandai COMPLETED.
             *
             * Backend saat ini hanya menyediakan GET Work Orders,
             * belum menyediakan endpoint untuk mengirim perubahan
             * status dari perangkat ke server.
             *
             * Karena itu kita tidak boleh menganggap perubahan lokal
             * sudah berhasil tersinkronisasi.
             */

            Result.success()

        } catch (exception: Exception) {

            Result.retry()
        }
    }

}