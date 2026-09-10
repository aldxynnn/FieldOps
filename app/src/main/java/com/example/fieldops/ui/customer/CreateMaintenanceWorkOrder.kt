package com.example.fieldops.ui.customer

import com.example.fieldops.data.model.CustomerAsset
import com.example.fieldops.data.model.PreventiveMaintenance
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus

fun createMaintenanceWorkOrder(
    maintenance: PreventiveMaintenance,
    asset: CustomerAsset,
    customerName: String,
    siteName: String,
    siteAddress: String,
    existingWorkOrders: List<WorkOrder>
): WorkOrder {

    val nextNumber =
        existingWorkOrders
            .mapNotNull { workOrder ->
                workOrder.id
                    .removePrefix("WO-")
                    .toIntOrNull()
            }
            .maxOrNull()
            ?.plus(1)
            ?: 1236

    return WorkOrder(
        id = "WO-${nextNumber.toString().padStart(6, '0')}",
        title = maintenance.maintenanceType,
        customer = customerName,
        location = "$siteName - $siteAddress",
        date = maintenance.nextServiceDate,
        time = "08:00",
        status = WorkOrderStatus.PENDING,
        priority = "MEDIUM"
    )

}