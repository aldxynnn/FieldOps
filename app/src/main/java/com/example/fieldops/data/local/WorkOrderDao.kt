package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderDao {

    @Query("SELECT * FROM work_orders ORDER BY id DESC")
    fun observeWorkOrders(): Flow<List<WorkOrderEntity>>

    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    fun observeWorkOrder(id: String): Flow<WorkOrderEntity?>

    @Query("SELECT COUNT(*) FROM work_orders")
    suspend fun getWorkOrderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkOrder(workOrder: WorkOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkOrders(workOrders: List<WorkOrderEntity>)

    @Delete
    suspend fun deleteWorkOrder(workOrder: WorkOrderEntity)

    @Query("DELETE FROM work_orders")
    suspend fun deleteAllWorkOrders()
}