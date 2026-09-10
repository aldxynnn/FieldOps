package com.example.fieldops.data.model

enum class WorkOrderStatus {
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class WorkOrder(
    val id: String,
    val title: String,
    val customer: String,
    val location: String,
    val date: String,
    val time: String,
    val status: WorkOrderStatus,
    val priority: String,
    val assetId: String? = null
)
