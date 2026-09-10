package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_order_photos"
)
data class WorkOrderPhotoEntity(
    @PrimaryKey
    val id: String,
    val workOrderId: String,
    val title: String,
    val filePath: String,
    val createdAt: Long
)