package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query(
        """
    SELECT * FROM notifications
    ORDER BY createdAt DESC
    """
    )
    fun observeNotifications():
            Flow<List<NotificationEntity>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertNotification(
        notification: NotificationEntity
    )

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertNotifications(
        notifications: List<NotificationEntity>
    )

    @Query(
        """
    UPDATE notifications
    SET isRead = 1
    WHERE id = :id
    """
    )
    suspend fun markAsRead(
        id: String
    )

    @Query(
        """
    UPDATE notifications
    SET isRead = 1
    WHERE isRead = 0
    """
    )
    suspend fun markAllAsRead()

    @Query(
        """
    SELECT COUNT(*)
    FROM notifications
    """
    )
    suspend fun getNotificationCount(): Int

    @Query(
        """
    DELETE FROM notifications
    """
    )
    suspend fun deleteAllNotifications()

}