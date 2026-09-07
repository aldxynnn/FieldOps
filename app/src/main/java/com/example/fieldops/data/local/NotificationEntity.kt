package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fieldops.ui.notification.FieldOpsNotification
import com.example.fieldops.ui.notification.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: Long
)

fun NotificationEntity.toNotification(): FieldOpsNotification {

    val notificationType =
        runCatching {
            NotificationType.valueOf(type)
        }.getOrDefault(
            NotificationType.SYSTEM
        )

    return FieldOpsNotification(
        id = id,
        title = title,
        description = description,
        time = time,
        type = notificationType,
        isRead = isRead
    )

}

fun FieldOpsNotification.toEntity(
    createdAt: Long = System.currentTimeMillis()
): NotificationEntity {

    return NotificationEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        type = type.name,
        isRead = isRead,
        createdAt = createdAt
    )

}