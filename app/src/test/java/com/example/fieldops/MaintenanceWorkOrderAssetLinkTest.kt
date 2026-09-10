package com.example.fieldops

import com.example.fieldops.data.model.Customer
import com.example.fieldops.data.model.CustomerAsset
import com.example.fieldops.data.model.CustomerSite
import com.example.fieldops.data.model.PreventiveMaintenance
import com.example.fieldops.data.model.PreventiveMaintenanceStatus
import com.example.fieldops.data.model.createMaintenanceWorkOrder
import com.example.fieldops.data.model.demoWorkOrders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MaintenanceWorkOrderAssetLinkTest {

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
            siteName = "Gedung Head Office Jakarta",
            address = "Jl. Sudirman No. 123, Jakarta",
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
    fun maintenance_work_order_contains_explicit_asset_id() {

        val maintenance =
            PreventiveMaintenance(
                id = "PM-ASSET-TEST",
                assetId = "asset_001",
                assetCode = "AC-JKT-001",
                maintenanceType = "Preventive Maintenance",
                intervalDays = 30,
                lastServiceDate = "2026-08-15",
                nextServiceDate = "2026-09-14",
                technician = "Andi Pratama",
                status = PreventiveMaintenanceStatus.DUE_SOON
            )

        val workOrder =
            createMaintenanceWorkOrder(
                maintenance = maintenance,
                customer = customer,
                site = site,
                asset = asset,
                existingWorkOrders = demoWorkOrders
            )

        assertNotNull(workOrder)

        assertEquals(
            "asset_001",
            workOrder.assetId
        )

        assertEquals(
            "PT ABC Manufacturing",
            workOrder.customer
        )

        assertEquals(
            "Preventive Maintenance",
            workOrder.title
        )

        assertEquals(
            "2026-09-14",
            workOrder.date
        )
    }

}