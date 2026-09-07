package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_order_notes"
)
data class WorkOrderNoteEntity(
    @PrimaryKey
    val id: String,
    val workOrderId: String,
    val note: String,
    val createdAt: Long
)