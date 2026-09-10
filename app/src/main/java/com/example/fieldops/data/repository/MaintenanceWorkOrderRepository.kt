package com.example.fieldops.data.repository

import com.example.fieldops.data.model.PreventiveMaintenance
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.buildMaintenanceWorkOrder

class MaintenanceWorkOrderRepository(
    private val workOrderRepository: WorkOrderRepository
) {

    suspend fun createFromMaintenance(
        maintenance: PreventiveMaintenance,
        existingWorkOrders: List<WorkOrder>
    ): WorkOrder? {

        val workOrder =
            buildMaintenanceWorkOrder(
                maintenance = maintenance,
                existingWorkOrders = existingWorkOrders
            )
                ?: return null

        workOrderRepository.updateWorkOrder(
            workOrder
        )

        return workOrder
    }

}