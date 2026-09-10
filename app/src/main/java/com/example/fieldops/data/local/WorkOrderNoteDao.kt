package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderNoteDao {

    @Query(
        """
    SELECT * FROM work_order_notes
    WHERE workOrderId = :workOrderId
    ORDER BY createdAt ASC
    """
    )
    fun observeNotes(
        workOrderId: String
    ): Flow<List<WorkOrderNoteEntity>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertNote(
        note: WorkOrderNoteEntity
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertNotes(
        notes: List<WorkOrderNoteEntity>
    )

    @Delete
    suspend fun deleteNote(
        note: WorkOrderNoteEntity
    )

    @Query(
        "DELETE FROM work_order_notes WHERE workOrderId = :workOrderId"
    )
    suspend fun deleteNotesForWorkOrder(
        workOrderId: String
    )

    @Query(
        "SELECT * FROM work_order_notes WHERE id = :id LIMIT 1"
    )
    suspend fun getNote(
        id: String
    ): WorkOrderNoteEntity?

}