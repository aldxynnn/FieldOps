package com.example.fieldops.data.model

fun createMaintenanceWorkOrder(
    maintenance: PreventiveMaintenance,
    customer: Customer,
    site: CustomerSite,
    asset: CustomerAsset,
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

    val generatedId =
        "WO-${nextNumber.toString().padStart(6, '0')}"

    return WorkOrder(
        id = generatedId,
        title = maintenance.maintenanceType,
        customer = customer.companyName,
        location = "${site.siteName} - ${site.address}",
        date = maintenance.nextServiceDate,
        time = "08:00",
        status = WorkOrderStatus.PENDING,
        priority = "MEDIUM",
        assetId = asset.id
    )

}

fun findMaintenanceContext(
    maintenance: PreventiveMaintenance
): Triple<Customer, CustomerSite, CustomerAsset>? = null

fun buildMaintenanceWorkOrder(
    maintenance: PreventiveMaintenance,
    existingWorkOrders: List<WorkOrder>
): WorkOrder? = null
