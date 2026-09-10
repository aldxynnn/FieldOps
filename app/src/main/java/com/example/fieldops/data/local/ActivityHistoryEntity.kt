package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fieldops.ui.activity.ActivityHistoryItem
import com.example.fieldops.ui.activity.ActivityType

@Entity(
    tableName = "activity_history"
)
data class ActivityHistoryEntity(
    @PrimaryKey
    val id: String,
    val workOrderId: String,
    val title: String,
    val description: String,
    val time: String,
    val type: String
)

fun ActivityHistoryEntity.toActivityHistoryItem(): ActivityHistoryItem {
    return ActivityHistoryItem(
        id = id,
        workOrderId = workOrderId,
        title = title,
        description = description,
        time = time,
        type = runCatching {
            ActivityType.valueOf(type)
        }.getOrDefault(
            ActivityType.STATUS
        )
    )
}

fun ActivityHistoryItem.toEntity(): ActivityHistoryEntity {
    return ActivityHistoryEntity(
        id = id,
        workOrderId = workOrderId,
        title = title,
        description = description,
        time = time,
        type = type.name
    )
}