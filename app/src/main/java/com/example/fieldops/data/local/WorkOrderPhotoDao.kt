package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderPhotoDao {

    @Query(
        """
    SELECT * FROM work_order_photos
    WHERE workOrderId = :workOrderId
    ORDER BY createdAt ASC
    """
    )
    fun observePhotos(
        workOrderId: String
    ): Flow<List<WorkOrderPhotoEntity>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertPhoto(
        photo: WorkOrderPhotoEntity
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertPhotos(
        photos: List<WorkOrderPhotoEntity>
    )

    @Delete
    suspend fun deletePhoto(
        photo: WorkOrderPhotoEntity
    )

    @Query(
        "DELETE FROM work_order_photos WHERE workOrderId = :workOrderId"
    )
    suspend fun deletePhotosForWorkOrder(
        workOrderId: String
    )

    @Query(
        "SELECT * FROM work_order_photos WHERE id = :id LIMIT 1"
    )
    suspend fun getPhoto(
        id: String
    ): WorkOrderPhotoEntity?

}