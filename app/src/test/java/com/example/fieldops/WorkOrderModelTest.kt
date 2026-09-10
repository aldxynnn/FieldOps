package com.example.fieldops

import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.model.demoWorkOrders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkOrderModelTest {

    @Test
    fun demoWorkOrdersContainExpectedData() {

        assertEquals(
            5,
            demoWorkOrders.size
        )

        assertEquals(
            "WO-001234",
            demoWorkOrders.first().id
        )

        assertEquals(
            "PT. Maju Bersama",
            demoWorkOrders.first().customer
        )
    }

    @Test
    fun demoWorkOrdersContainMainStatuses() {

        val statuses =
            demoWorkOrders
                .map { it.status }
                .toSet()

        assertTrue(
            statuses.contains(
                WorkOrderStatus.PENDING
            )
        )

        assertTrue(
            statuses.contains(
                WorkOrderStatus.ACCEPTED
            )
        )

        assertTrue(
            statuses.contains(
                WorkOrderStatus.IN_PROGRESS
            )
        )

        assertTrue(
            statuses.contains(
                WorkOrderStatus.COMPLETED
            )
        )
    }

    @Test
    fun completedWorkOrderHasCompletedStatus() {

        val completedWorkOrder =
            demoWorkOrders.find {
                it.id == "WO-001232"
            }

        assertNotNull(
            completedWorkOrder
        )

        assertEquals(
            WorkOrderStatus.COMPLETED,
            completedWorkOrder?.status
        )
    }

    @Test
    fun pendingWorkOrderCanMoveToAccepted() {

        val pendingWorkOrder =
            demoWorkOrders.find {
                it.status == WorkOrderStatus.PENDING
            }

        assertNotNull(
            pendingWorkOrder
        )

        val updatedWorkOrder =
            pendingWorkOrder!!.copy(
                status = WorkOrderStatus.ACCEPTED
            )

        assertEquals(
            WorkOrderStatus.ACCEPTED,
            updatedWorkOrder.status
        )

        assertEquals(
            pendingWorkOrder.id,
            updatedWorkOrder.id
        )
    }

    @Test
    fun acceptedWorkOrderCanMoveToInProgress() {

        val acceptedWorkOrder =
            demoWorkOrders.find {
                it.status == WorkOrderStatus.ACCEPTED
            }

        assertNotNull(
            acceptedWorkOrder
        )

        val updatedWorkOrder =
            acceptedWorkOrder!!.copy(
                status = WorkOrderStatus.IN_PROGRESS
            )

        assertEquals(
            WorkOrderStatus.IN_PROGRESS,
            updatedWorkOrder.status
        )
    }

    @Test
    fun inProgressWorkOrderCanBeCompleted() {

        val inProgressWorkOrder =
            demoWorkOrders.find {
                it.status == WorkOrderStatus.IN_PROGRESS
            }

        assertNotNull(
            inProgressWorkOrder
        )

        val updatedWorkOrder =
            inProgressWorkOrder!!.copy(
                status = WorkOrderStatus.COMPLETED
            )

        assertEquals(
            WorkOrderStatus.COMPLETED,
            updatedWorkOrder.status
        )
    }

}