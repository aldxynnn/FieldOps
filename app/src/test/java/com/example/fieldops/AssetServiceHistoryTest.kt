package com.example.fieldops

import com.example.fieldops.data.model.AssetServiceHistoryItem
import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.CustomerAsset
import com.example.fieldops.data.model.CustomerSite
import com.example.fieldops.data.model.WorkOrder
import com.example.fieldops.data.model.WorkOrderStatus
import com.example.fieldops.data.model.buildAssetServiceHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetServiceHistoryTest {

    private val asset =
        CustomerAsset(
            id = "asset_001",
            siteId = "site_001",
            assetCode = "AC-JKT-001",
            name = "Air Conditioner Ruang Server",
            brand = "Daikin",
            model = "FTKC35",
            serialNumber = "SN-AC-001",
            category = "HVAC",
            installationDate = "2024-01-15",
            status = "ACTIVE"
        )

    private val site =
        CustomerSite(
            id = "site_001",
            customerId = "customer_001",
            siteName = "Site A - Jakarta",
            address = "Jakarta",
            city = "Jakarta",
            contactPerson = "Budi",
            phone = "081234567890",
            latitude = -6.200000,
            longitude = 106.816666,
            assets = listOf(asset)
        )

    private val customer =
        Customer(
            id = "customer_001",
            companyName = "PT ABC Manufacturing",
            industry = "Manufacturing",
            contactPerson = "Budi",
            phone = "021-5550001",
            email = "contact@abc.co.id",
            address = "Jakarta",
            sites = listOf(site)
        )

    @Test
    fun history_only_contains_work_orders_for_same_asset() {

        val matchingWorkOrder =
            WorkOrder(
                id = "WO-002001",
                title = "Preventive Maintenance",
                customer = "PT ABC Manufacturing",
                location = "Site A - Jakarta",
                date = "2026-09-14",
                time = "08:00",
                status = WorkOrderStatus.PENDING,
                priority = "MEDIUM",
                assetId = "asset_001"
            )

        val differentAssetWorkOrder =
            WorkOrder(
                id = "WO-002002",
                title = "Repair",
                customer = "PT ABC Manufacturing",
                location = "Site A - Jakarta",
                date = "2026-09-15",
                time = "09:00",
                status = WorkOrderStatus.COMPLETED,
                priority = "HIGH",
                assetId = "asset_002"
            )

        val history =
            buildAssetServiceHistory(
                asset = asset,
                customer = customer,
                site = site,
                workOrders =
                    listOf(
                        matchingWorkOrder,
                        differentAssetWorkOrder
                    )
            )

        assertEquals(
            1,
            history.size
        )

        assertEquals(
            "WO-002001",
            history.first().workOrderId
        )

        assertEquals(
            "asset_001",
            matchingWorkOrder.assetId
        )

        assertEquals(
            "PT ABC Manufacturing",
            history.first().customerName
        )

        assertEquals(
            "Site A - Jakarta",
            history.first().siteName
        )

        assertEquals(
            "AC-JKT-001",
            history.first().assetCode
        )
    }

    @Test
    fun work_order_without_asset_id_is_not_asset_history() {

        val legacyWorkOrder =
            WorkOrder(
                id = "WO-002003",
                title = "Legacy Service",
                customer = "PT ABC Manufacturing",
                location = "Site A - Jakarta",
                date = "2026-09-16",
                time = "10:00",
                status = WorkOrderStatus.COMPLETED,
                priority = "LOW",
                assetId = null
            )

        val history =
            buildAssetServiceHistory(
                asset = asset,
                customer = customer,
                site = site,
                workOrders =
                    listOf(
                        legacyWorkOrder
                    )
            )

        assertTrue(
            history.isEmpty()
        )
    }

    @Test
    fun history_metrics_are_calculated_correctly() {

        val history =
            listOf(
                AssetServiceHistoryItem(
                    workOrderId = "WO-1",
                    title = "Service",
                    date = "2026-09-10",
                    time = "08:00",
                    status = WorkOrderStatus.COMPLETED,
                    customerName = "PT ABC Manufacturing",
                    siteName = "Site A",
                    assetCode = "AC-JKT-001",
                    assetName = "Air Conditioner Ruang Server",
                    brand = "Daikin",
                    model = "FTKC35",
                    serialNumber = "SN-AC-001"
                ),
                AssetServiceHistoryItem(
                    workOrderId = "WO-2",
                    title = "Inspection",
                    date = "2026-09-11",
                    time = "09:00",
                    status = WorkOrderStatus.IN_PROGRESS,
                    customerName = "PT ABC Manufacturing",
                    siteName = "Site A",
                    assetCode = "AC-JKT-001",
                    assetName = "Air Conditioner Ruang Server",
                    brand = "Daikin",
                    model = "FTKC35",
                    serialNumber = "SN-AC-001"
                ),
                AssetServiceHistoryItem(
                    workOrderId = "WO-3",
                    title = "Maintenance",
                    date = "2026-09-12",
                    time = "10:00",
                    status = WorkOrderStatus.PENDING,
                    customerName = "PT ABC Manufacturing",
                    siteName = "Site A",
                    assetCode = "AC-JKT-001",
                    assetName = "Air Conditioner Ruang Server",
                    brand = "Daikin",
                    model = "FTKC35",
                    serialNumber = "SN-AC-001"
                )
            )

        val completed =
            history.count {
                it.status == WorkOrderStatus.COMPLETED
            }

        val active =
            history.count {
                it.status == WorkOrderStatus.IN_PROGRESS ||
                        it.status == WorkOrderStatus.ACCEPTED
            }

        val pending =
            history.count {
                it.status == WorkOrderStatus.PENDING
            }

        val cancelled =
            history.count {
                it.status == WorkOrderStatus.CANCELLED
            }

        assertEquals(
            1,
            completed
        )

        assertEquals(
            1,
            active
        )

        assertEquals(
            1,
            pending
        )

        assertEquals(
            0,
            cancelled
        )
    }

}