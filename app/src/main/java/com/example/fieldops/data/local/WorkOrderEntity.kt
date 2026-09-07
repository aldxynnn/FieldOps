package com.example.fieldops.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

@Entity(tableName = "work_orders")
data class WorkOrderEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val customer: String,
    val location: String,
    val date: String,
    val time: String,
    val status: String,
    val priority: String
)

fun WorkOrderEntity.toWorkOrder(): WorkOrder {
    return WorkOrder(
        id = id,
        title = title,
        customer = customer,
        location = location,
        date = date,
        time = time,
        status = WorkOrderStatus.valueOf(status),
        priority = priority
    )
}

fun WorkOrder.toEntity(): WorkOrderEntity {
    return WorkOrderEntity(
        id = id,
        title = title,
        customer = customer,
        location = location,
        date = date,
        time = time,
        status = status.name,
        priority = priority
    )
}