package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityHistoryDao {

    @Query(
        """
    SELECT * FROM activity_history
    ORDER BY rowid DESC
    """
    )
    fun observeActivities(): Flow<List<ActivityHistoryEntity>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertActivity(
        activity: ActivityHistoryEntity
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertActivities(
        activities: List<ActivityHistoryEntity>
    )

    @Query(
        """
    DELETE FROM activity_history
    """
    )
    suspend fun deleteAllActivities()

    @Query(
        """
    DELETE FROM activity_history
    WHERE workOrderId = :workOrderId
    """
    )
    suspend fun deleteActivitiesForWorkOrder(
        workOrderId: String
    )

}