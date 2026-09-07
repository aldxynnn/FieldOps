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

            /*
             * Ambil antrean perubahan lokal.
             *
             * Karena backend saat ini belum memiliki
             * endpoint untuk push perubahan status,
             * operation PENDING tidak dihapus atau
             * dianggap selesai.
             */
            val pendingOperations =
                database.syncOperationDao()
                    .getPendingOperations()

            /*
             * Simpan ID Work Order yang memiliki perubahan
             * lokal yang belum tersinkron.
             *
             * Data server untuk Work Order tersebut tidak
             * boleh menimpa perubahan lokal pengguna.
             */
            val pendingWorkOrderIds =
                pendingOperations
                    .map {
                        it.workOrderId
                    }
                    .toSet()

            /*
             * Ambil Work Order terbaru dari server.
             */
            val remoteWorkOrders =
                RetrofitClient.api.getWorkOrders()

            /*
             * Simpan data server ke Room hanya untuk
             * Work Order yang tidak sedang memiliki
             * perubahan lokal PENDING.
             *
             * Ini mencegah status lokal tertimpa oleh
             * data server yang masih belum mengetahui
             * perubahan terbaru pengguna.
             */
            if (remoteWorkOrders.isNotEmpty()) {

                val entities =
                    remoteWorkOrders
                        .filter { workOrder ->
                            workOrder.id !in pendingWorkOrderIds
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
                                priority = workOrder.priority
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
             * Jangan menghapus operation PENDING.
             *
             * Queue tetap menunggu sampai backend menyediakan
             * endpoint push untuk mengirim perubahan tersebut.
             *
             * Worker tetap dianggap berhasil karena koneksi
             * dan proses pull dari server berhasil dilakukan.
             */
            Result.success()

        } catch (exception: Exception) {

            /*
             * Jika server/network gagal, WorkManager akan
             * mencoba kembali sesuai mekanisme retry.
             */
            Result.retry()
        }
    }
}