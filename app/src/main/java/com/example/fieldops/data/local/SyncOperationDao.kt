package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncOperationDao {

    @Query(
        """
        SELECT * FROM sync_operations
        WHERE status = 'PENDING'
        ORDER BY createdAt ASC
        """
    )
    fun observePendingOperations(): Flow<List<SyncOperationEntity>>

    @Query(
        """
        SELECT * FROM sync_operations
        WHERE status = 'PENDING'
        ORDER BY createdAt ASC
        """
    )
    suspend fun getPendingOperations(): List<SyncOperationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(
        operation: SyncOperationEntity
    )

    @Query(
        "UPDATE sync_operations SET status = :status WHERE id = :id"
    )
    suspend fun updateStatus(
        id: Long,
        status: String
    )

    @Query("DELETE FROM sync_operations WHERE id = :id")
    suspend fun deleteOperation(
        id: Long
    )

    @Query("DELETE FROM sync_operations")
    suspend fun deleteAllOperations()
}